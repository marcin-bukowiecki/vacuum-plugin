package io.vacuum.codesmells.values

import com.goide.psi.GoParameterDeclaration
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer

/**
 * @author Marcin Bukowiecki
 */
class FunctionContext {

    var pointerParameters = mutableListOf<SmartPsiElementPointer<GoParameterDeclaration>>()

    fun addParameter(param: GoParameterDeclaration) {
        pointerParameters.add(SmartPointerManager.createPointer(param))
    }

    fun removeIfExists(name: String): Boolean {
        var removed = false
        pointerParameters = pointerParameters.filter {
            removed = it.element?.paramDefinitionList?.first()?.name == name
            !removed
        }.toMutableList()
        return removed
    }
}