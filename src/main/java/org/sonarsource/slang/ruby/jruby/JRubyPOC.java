package org.sonarsource.slang.ruby.jruby;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.Collections;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyHash;
import org.jruby.RubyIO;
import org.jruby.ast.Node;
import org.jruby.common.IRubyWarnings;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.lexer.GetsLexerSource;
import org.jruby.lexer.LexerSource;
import org.jruby.lexer.yacc.RubyLexer;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.parser.ParserConfiguration;
import org.jruby.parser.RubyParser;
import org.jruby.parser.RubyParserResult;
import org.jruby.runtime.builtin.IRubyObject;

public class JRubyPOC {

  private final Ruby runtime = JavaEmbedUtils.initialize(Collections.emptyList());

  public static void main(String[] args) {
    String codeString = "def foo(bar)\n bar # comment \n end \n foo('astring')";
    Node node = new JRubyPOC().parseContents(codeString);
    System.out.println(node);
  }

  public Node parseContents(String content) {
    String file = "filename";
    ParserConfiguration configuration = new ParserConfiguration(runtime, 0, false, true, false);

    RubyArray list = getLines(configuration, runtime, file);

    ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
    RubyIO io = RubyIO.newIO(runtime, Channels.newChannel(bais));
    LexerSource lexerSource = new GetsLexerSource(file, configuration.getLineNumber(), io, list, configuration.getDefaultEncoding());
    Node node = null;

    try {
      node = parse(lexerSource, configuration);
    } finally {
      // In case of GetsLexerSource we actually will dispatch to gets which will increment $.
      // We do not want that in the case of raw parsing.
      runtime.setCurrentLine(0);
    }

    // Shutdown and terminate instance
    JavaEmbedUtils.terminate(runtime);

    return node;
  }

  @SuppressWarnings("unchecked")
  public Node parse(LexerSource lexerSource, ParserConfiguration configuration) {
    RubyParser2 parser = new RubyParser2(lexerSource, runtime.getWarnings());
    RubyParserResult result;
    try {
      result = parser.parse(configuration);
    } catch (IOException e) {
      // Enebo: We may want to change this error to be more specific,
      // but I am not sure which conditions leads to this...so lame message.
      throw runtime.newSyntaxError("Problem reading source: " + e);
    } catch (SyntaxException e) {
      switch (e.getPid()) {
        case UNKNOWN_ENCODING:
        case NOT_ASCII_COMPATIBLE:
          throw runtime.newArgumentError(e.getMessage());
        default:
          StringBuilder buffer = new StringBuilder(100);
          buffer.append(e.getFile()).append(':');
          buffer.append(e.getLine() + 1).append(": ");
          buffer.append(e.getMessage());

          throw runtime.newSyntaxError(buffer.toString());
      }
    }

    // If variables were added then we may need to grow the dynamic scope to match the static
    // one.
    // FIXME: Make this so we only need to check this for blockScope != null. We cannot
    // currently since we create the DynamicScope for a LocalStaticScope before parse begins.
    // Refactoring should make this fixable.
    if (result.getScope() != null) {
      result.getScope().growIfNeeded();
    }

    Node ast = result.getAST();
    return ast;
  }

  private RubyArray getLines(ParserConfiguration configuration, Ruby runtime, String file) {
    RubyArray list = null;
    IRubyObject scriptLines = runtime.getObject().getConstantAt("SCRIPT_LINES__");
    if (!configuration.isEvalParse() && scriptLines != null) {
      if (scriptLines instanceof RubyHash) {
        list = runtime.newArray();
        ((RubyHash) scriptLines).op_aset(runtime.getCurrentContext(), runtime.newString(file), list);
      }
    }
    return list;
  }

  class RubyParser2 extends RubyParser {

    public RubyParser2(LexerSource source, IRubyWarnings warnings) {
      super(source, warnings);
    }

    public RubyParserResult parse(ParserConfiguration configuration) throws IOException {
      support.reset();
      support.setConfiguration(configuration);
      support.setResult(new RubyParserResult());

      yyparse(lexer, new YYCallback(this));

      return support.getResult();
    }

    public RubyLexer getLexer() {
      return lexer;
    }
  }

}
