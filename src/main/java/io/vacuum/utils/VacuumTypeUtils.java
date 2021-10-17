/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.utils;

import com.goide.psi.GoCompositeType;
import com.goide.psi.GoType;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * @author Marcin Bukowiecki
 */
public class VacuumTypeUtils {

  @Contract("null, _ -> false")
  public static boolean isInt8(@Nullable GoType type, @Nullable PsiElement context) {
    return isBuiltinType(type, context, "int8");
  }

  @Contract("null, _ -> false")
  public static boolean isInt16(@Nullable GoType type, @Nullable PsiElement context) {
    return isBuiltinType(type, context, "int16");
  }

  @Contract("null, _ -> false")
  public static boolean isInt32(@Nullable GoType type, @Nullable PsiElement context) {
    return isBuiltinType(type, context, "int32");
  }

  @Contract("null, _, _ -> false; _, _, null -> false")
  private static boolean isBuiltinType(@Nullable GoType type, @Nullable PsiElement context, @Nullable String builtinTypeName) {
    if (builtinTypeName == null) {
      return false;
    } else {
      type = type != null ? type.getUnderlyingType(context) : null;
      return type != null && !(type instanceof GoCompositeType) && type.textMatches(builtinTypeName) && GoPsiImplUtil.builtin(type);
    }
  }
}
