package org.sonarsource.slang.ruby.jrubyparser;

import java.io.StringReader;
import org.jrubyparser.CompatVersion;
import org.jrubyparser.Parser;
import org.jrubyparser.ast.Node;
import org.jrubyparser.parser.ParserConfiguration;

public class JRubyParserJavaLibPOC {

  public static void main(String[] args) {
    String codeString = " # comment \ndef foo(bar)\n bar \n end\n foo('astring')";
    Node node = parseContents(codeString);
    System.out.println(node);
  }

  public static Node parseContents(String string) {
    Parser rubyParser = new Parser();
    StringReader in = new StringReader(string);
    CompatVersion version = CompatVersion.RUBY2_3;
    ParserConfiguration config = new ParserConfiguration(0, version);
    return rubyParser.parse("<code>", in, config);
  }

}
