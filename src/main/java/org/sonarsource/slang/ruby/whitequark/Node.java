package org.sonarsource.slang.ruby.whitequark;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

public class Node {

  private final Ruby runtime;
  private final Object rubyObject;

  public Node(Ruby runtime, Object rubyObject) {
    this.runtime = runtime;
    this.rubyObject = rubyObject;
  }

  public List<Node> getChildren() {
    if (isNode()) {
      List<Object> rubyAST = (List<Object>) JavaEmbedUtils.invokeMethod(runtime, rubyObject, "to_a", null, IRubyObject.class);
      return rubyAST.stream().map(rubyObject -> new Node(runtime, rubyObject)).collect(Collectors.toList());
    } else {
      return Collections.emptyList();
    }
  }

  public boolean isNode() {
    return rubyObject instanceof IRubyObject && "Node".equals(((IRubyObject) rubyObject).getType().getBaseName());
  }

  public boolean isLeaf() {
    return !isNode();
  }

  public Object getType() {
    if (isNode()) {
      return JavaEmbedUtils.invokeMethod(runtime, rubyObject, "type", null, IRubyObject.class);
    }
    return null;
  }

  public Object getValue() {
    if (isLeaf()) {
      return rubyObject;
    }
    return null;
  }

  public Object getLocation() {
    if (isNode()) {
      return JavaEmbedUtils.invokeMethod(runtime, rubyObject, "location", null, IRubyObject.class);
    } else {
      return null;
    }
  }

  @Override
  public String toString() {
    return "Node{rubyNode=" + rubyObject + '}';
  }
}
