package org.sonarsource.slang.ruby.whitequark;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.jruby.Ruby;
import org.jruby.RubyRuntimeAdapter;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.sonarsource.slang.ruby.whitequark.visitors.TreePrinter;

public class RubyParserPOC {

  public static void main(String[] args) throws IOException {
    String content = "#comment\n2 + 2\na = 2";
    new RubyParserPOC().parseWithRubyProcessor(content);
  }

  public void parseWithAST(String content) {
    URL resource1 = RubyParserPOC.class.getResource("/ast-2.4.0/lib");
    URL resource2 = RubyParserPOC.class.getResource("/parser-2.5.1.2/lib");
    if (resource1 == null || resource2 == null) {
      System.out.println("Rubygems not found");
      return;
    }

    Ruby runtime = JavaEmbedUtils.initialize(Arrays.asList(resource1.toString(), resource2.toString()));
    Object[] parameters = {content};

    String bootstrap = "require 'parser/current'\n" +
      "Parser::Builders::Default.emit_lambda   = true\n" +
      "Parser::Builders::Default.emit_procarg0 = true\n" +
      "Parser::Builders::Default.emit_encoding = true\n" +
      "Parser::Builders::Default.emit_index    = true\n" +
      "Parser::CurrentRuby";

    RubyRuntimeAdapter rubyRuntimeAdapter = JavaEmbedUtils.newRuntimeAdapter();
    IRubyObject rubyParser = rubyRuntimeAdapter.eval(runtime, bootstrap);
    List rubyASTAndComments = (List) JavaEmbedUtils.invokeMethod(runtime, rubyParser, "parse_with_comments", parameters, List.class);

    // System.out.println(rubyASTAndComments.get(0)); // AST
    // System.out.println(rubyASTAndComments.get(1)); // COMMENTS
    System.out.println(TreePrinter.tree2string(new Node(runtime, rubyASTAndComments.get(0))));

    // Shutdown and terminate instance
    JavaEmbedUtils.terminate(runtime);
  }

  public void parseWithRubyProcessor(String content) {
    URL resource1 = RubyParserPOC.class.getResource("/ast-2.4.0/lib");
    URL resource2 = RubyParserPOC.class.getResource("/parser-2.5.1.2/lib");
    if (resource1 == null || resource2 == null) {
      System.out.println("Rubygems not found");
      return;
    }

    Ruby runtime = JavaEmbedUtils.initialize(Arrays.asList(resource1.toString(), resource2.toString()));

    String bootstrap = "require 'parser/current'\n" +
      "require 'parser/ast/processor'\n" +
      "Parser::Builders::Default.emit_lambda   = true\n" +
      "Parser::Builders::Default.emit_procarg0 = true\n" +
      "Parser::Builders::Default.emit_encoding = true\n" +
      "Parser::Builders::Default.emit_index    = true\n";

    RubyRuntimeAdapter rubyRuntimeAdapter = JavaEmbedUtils.newRuntimeAdapter();
    rubyRuntimeAdapter.eval(runtime, bootstrap);

    ASTProcessor.initASTProcessor(runtime);

    String visitEval = "Parser::AST::ASTProcessor.new.process(Parser::CurrentRuby.parse('a = 2 && 2'))";
    rubyRuntimeAdapter.eval(runtime, visitEval);

    // Shutdown and terminate instance
    JavaEmbedUtils.terminate(runtime);
  }

}
