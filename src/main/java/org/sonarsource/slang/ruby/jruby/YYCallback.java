package org.sonarsource.slang.ruby.jruby;

import org.jruby.parser.YYDebug;

public class YYCallback extends YYDebug {

  private JRubyPOC.RubyParser2 parser2;

  public YYCallback(JRubyPOC.RubyParser2 parser2) {
    this.parser2 = parser2;
  }

  @Override
  public void accept(Object a) {
    System.out.println("accept:" + a);
    System.out.println("tokp: " + parser2.getLexer().tokp + "; lex_p: " + parser2.getLexer().lex_p);
  }

  public void lex(int a, int b, String c, Object d) {
    System.out.println("lex:" + a + "; " + b + "; " + c + "; " + d);
    System.out.println("tokp: " + parser2.getLexer().tokp + "; lex_p: " + parser2.getLexer().lex_p);
  }

}
