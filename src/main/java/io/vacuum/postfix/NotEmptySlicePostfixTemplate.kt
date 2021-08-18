/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.postfix

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider

/**
 * @author Marcin Bukowiecki
 */
class NotEmptySlicePostfixTemplate(provider: PostfixTemplateProvider) :
    BaseSlicePostfixTemplate("Vacuum.Slice.isNotEmpty", "isNotEmpty", "len(array) != 0", provider) {

    override fun getOperator(): String {
        return "!="
    }
}