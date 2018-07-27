/*
 * SonarSource SLang
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.slang.ruby.whitequark.visitors;

import java.util.ArrayDeque;
import java.util.Deque;
import org.sonarsource.slang.ruby.whitequark.Node;

public class TreeContext {

  private final Deque<Node> ancestors;
  private Node current;

  public TreeContext() {
    ancestors = new ArrayDeque<>();
  }

  public Deque<Node> ancestors() {
    return ancestors;
  }

  protected void before(Node root) {
    ancestors.clear();
  }

  public void enter(Node node) {
    if (current != null) {
      ancestors.push(current);
    }
    current = node;
  }

  public void leave(Node node) {
    if (!ancestors.isEmpty()) {
      current = ancestors.pop();
    }
  }

}
