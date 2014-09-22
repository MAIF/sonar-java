/*
 * SonarQube Java
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.java.model.declaration;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import org.sonar.java.ast.parser.JavaGrammar;
import org.sonar.java.model.JavaTree;
import org.sonar.plugins.java.api.tree.AnnotationTree;
import org.sonar.plugins.java.api.tree.ModifierKeywordTree;
import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.ModifierTree;
import org.sonar.plugins.java.api.tree.ModifiersTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TreeVisitor;

import java.util.Iterator;
import java.util.List;

public class ModifiersTreeImpl extends JavaTree implements ModifiersTree {
  // TODO remove:
  public static final org.sonar.java.model.declaration.ModifiersTreeImpl EMPTY =
    new ModifiersTreeImpl((AstNode) null, ImmutableList.<ModifierKeywordTree>of(), ImmutableList.<AnnotationTree>of());


  /* FIXME */
  public static final org.sonar.java.model.declaration.ModifiersTreeImpl emptyModifiers() {
    return new ModifiersTreeImpl(ImmutableList.<ModifierTree>of());
  }

  private final List<Modifier> modifiers;
  private final List<ModifierTree> modifierTrees;
  private final List<AnnotationTree> annotations;

  private ModifiersTreeImpl(AstNode astNode, List<ModifierKeywordTree> modifierKeywordTrees, List<AnnotationTree> annotations) {
    super(astNode);
    this.annotations = Preconditions.checkNotNull(annotations);
    modifiers = Lists.newArrayList();
    modifierTrees = Lists.newArrayList();
  }

  public ModifiersTreeImpl(List<ModifierTree> javaTrees) {
    super(JavaGrammar.MODIFIERS);
    ImmutableList.Builder<Modifier> modifiers = ImmutableList.builder();
    ImmutableList.Builder<AnnotationTree> annotations = ImmutableList.builder();
    for (ModifierTree modifierTree : javaTrees) {
      if (modifierTree.is(Kind.ANNOTATION)) {
        annotations.add((AnnotationTree) modifierTree);
      } else {
        ModifierKeywordTree modifierKeywordTree = (ModifierKeywordTree) modifierTree;
        modifiers.add(modifierKeywordTree.modifier());
      }
      addChild((AstNode) modifierTree);
    }
    this.annotations = annotations.build();
    this.modifierTrees = javaTrees;
    this.modifiers = modifiers.build();
  }


  @Override
  public Kind getKind() {
    return Kind.MODIFIERS;
  }

  @Override
  public List<Modifier> modifiers() {
    return modifiers;
  }

  @Override
  public List<ModifierTree> list() {
    return modifierTrees;
  }

  @Override
  public List<AnnotationTree> annotations() {
    return annotations;
  }

  @Override
  public void accept(TreeVisitor visitor) {
    visitor.visitModifier(this);
  }

  @Override
  public Iterator<Tree> childrenIterator() {
    return ImmutableList.<Tree>builder().addAll(list()).build().iterator();
  }

}
