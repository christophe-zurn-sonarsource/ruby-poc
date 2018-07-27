package org.sonarsource.slang.ruby.whitequark;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.internal.runtime.methods.DynamicMethod;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

@JRubyClass(name = "ASTProcessor")
public class ASTProcessor extends RubyObject {

  public static RubyClass superClass;

  public static void initASTProcessor(final Ruby runtime) {
    RubyModule topModule = runtime.defineModule("Parser");
    RubyModule astModule = topModule.defineModuleUnder("AST");
    superClass = runtime.getObject().subclasses(true).stream()
      .filter(clazz -> "Parser::AST::Processor".equals(clazz.getName()))
      .findFirst()
      .orElse(runtime.getObject());
    RubyModule astConverter = astModule.defineClassUnder(ASTProcessor.class.getSimpleName(), superClass, ASTProcessor::new);
    astConverter.defineAnnotatedMethods(ASTProcessor.class);
  }

  public ASTProcessor(Ruby runtime, RubyClass metaClass) {
    super(runtime, metaClass);
    if (superClass == null) {
      throw new IllegalStateException("ASTProcessor should be added to ruby runtime first");
    }
  }

  @JRubyMethod(name = "new", meta = true, rest = true)
  public static IRubyObject rbNew(ThreadContext context, IRubyObject klazz, IRubyObject[] args) {
    ASTProcessor astProcessor = (ASTProcessor) ((RubyClass) klazz).allocate();
    return astProcessor;
  }

  @JRubyMethod(name = "process")
  public IRubyObject process(ThreadContext context, IRubyObject arg1) {
    IRubyObject rubyObject = callSuperMethod(context, "process", arg1);
    System.out.println(rubyObject);
    return rubyObject;
  }

  @JRubyMethod(name = "process_regular_node")
  public IRubyObject process_regular_node(ThreadContext context, IRubyObject arg1) {
    IRubyObject rubyObject = callSuperMethod(context, "process_regular_node", arg1);
    System.out.println(rubyObject);
    return rubyObject;
  }

  @JRubyMethod(name = "on_var")
  public IRubyObject on_var(ThreadContext context, IRubyObject arg1) {
    IRubyObject rubyObject = callSuperMethod(context, "on_var", arg1);
    System.out.println(rubyObject);
    return rubyObject;
  }

  @JRubyMethod(name = "on_vasgn")
  public IRubyObject on_vasgn(ThreadContext context, IRubyObject arg1) {
    IRubyObject rubyObject = callSuperMethod(context, "on_vasgn", arg1);
    System.out.println(rubyObject);
    return rubyObject;
  }

  @JRubyMethod(name = "on_argument")
  public IRubyObject on_argument(ThreadContext context, IRubyObject arg1) {
    IRubyObject rubyObject = callSuperMethod(context, "on_argument", arg1);
    System.out.println(rubyObject);
    return rubyObject;
  }

  private IRubyObject callSuperMethod(ThreadContext context, String methodName, IRubyObject arg1) {
    DynamicMethod method = superClass.searchMethod(methodName);
    return method.call(context, this, superClass, methodName, arg1);
  }

}
