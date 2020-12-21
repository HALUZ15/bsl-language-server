/*
 * This file is a part of BSL Language Server.
 *
 * Copyright © 2018-2020
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com> and contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * BSL Language Server is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * BSL Language Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BSL Language Server.
 */
package com.github._1c_syntax.bsl.languageserver.context.symbol;

import com.github._1c_syntax.bsl.languageserver.utils.Ranges;
import com.github._1c_syntax.bsl.languageserver.utils.Trees;
import com.github._1c_syntax.bsl.parser.BSLParser;
import com.github._1c_syntax.bsl.parser.BSLParserRuleContext;
import lombok.Getter;
import lombok.Value;
import org.eclipse.lsp4j.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Value
public class SymbolTree {
  List<SourceDefinedSymbol> children;

  @Getter(lazy = true)
  List<SourceDefinedSymbol> childrenFlat = createChildrenFlat();

  @Getter(lazy = true)
  List<MethodSymbol> methods = createMethods();

  public <T> List<T> getChildrenFlat(Class<T> clazz) {
    return getChildrenFlat().stream()
      .filter(clazz::isInstance)
      .map(clazz::cast)
      .collect(Collectors.toList());
  }

  public List<RegionSymbol> getModuleLevelRegions() {
    return getChildren().stream()
      .filter(RegionSymbol.class::isInstance)
      .map(symbol -> (RegionSymbol) symbol)
      .collect(Collectors.toList());
  }

  public List<RegionSymbol> getRegionsFlat() {
    return getChildrenFlat(RegionSymbol.class);
  }

  public Optional<MethodSymbol> getMethodSymbol(BSLParserRuleContext ctx) {
    BSLParserRuleContext subNameNode;
    if (Trees.nodeContainsErrors(ctx)) {
      subNameNode = ctx;
    } else if (ctx instanceof BSLParser.SubContext) {
      if (((BSLParser.SubContext) ctx).function() == null) {
        subNameNode = ((BSLParser.SubContext) ctx).procedure().procDeclaration().subName();
      } else {
        subNameNode = ((BSLParser.SubContext) ctx).function().funcDeclaration().subName();
      }
    } else {
      subNameNode = ctx;
    }

    Range subNameRange = Ranges.create(subNameNode);

    return getMethods().stream()
      .filter(methodSymbol -> methodSymbol.getSubNameRange().equals(subNameRange))
      .findAny();
  }

  /**
   * Поиск MethodSymbol в дереве по указанному имени (без учета регистра).
   *
   * @param methodName Имя метода
   * @return MethodSymbol, если он был найден в дереве символов.
   */
  public Optional<MethodSymbol> getMethodSymbol(String methodName) {
    return getMethods().stream()
      .filter(methodSymbol -> methodName.equalsIgnoreCase(methodSymbol.getName()))
      .findAny();
  }

  /**
   * Поиск метода, включающего переданный Range
   *
   * @param range Область для поиска
   * @return MethodSymbol, если он был найден в дереве символов.
   */
  public Optional<MethodSymbol> getMethodSymbol(Range range) {
    return getMethods().stream()
      .filter(methodSymbol -> Ranges.containsRange(methodSymbol.getRange(), range))
      .findAny();
  }

  public List<VariableSymbol> getVariables() {
    return getChildrenFlat(VariableSymbol.class);
  }

  public Optional<VariableSymbol> getVariableSymbol(BSLParserRuleContext ctx) {

    BSLParserRuleContext varNameNode;

    if (Trees.nodeContainsErrors(ctx)) {
      varNameNode = ctx;
    } else if (ctx instanceof BSLParser.ModuleVarDeclarationContext) {
      varNameNode = ((BSLParser.ModuleVarDeclarationContext) ctx).var_name();
    } else if (ctx instanceof BSLParser.SubVarDeclarationContext) {
      varNameNode = ((BSLParser.SubVarDeclarationContext) ctx).var_name();
    } else {
      varNameNode = ctx;
    }

    Range variableNameRange = Ranges.create(varNameNode);

    return getVariables().stream()
      .filter(variableSymbol -> variableSymbol.getVariableNameRange().equals(variableNameRange))
      .findAny();
  }

  private List<SourceDefinedSymbol> createChildrenFlat() {
    List<SourceDefinedSymbol> symbols = new ArrayList<>();
    getChildren().forEach(child -> flatten(child, symbols));

    return symbols;
  }

  private List<MethodSymbol> createMethods() {
    return getChildrenFlat(MethodSymbol.class);
  }

  private static void flatten(SourceDefinedSymbol symbol, List<SourceDefinedSymbol> symbols) {
    symbols.add(symbol);
    symbol.getChildren().forEach(child -> flatten(child, symbols));
  }

}
