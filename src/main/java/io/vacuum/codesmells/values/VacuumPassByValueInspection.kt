package io.vacuum.codesmells.values

import com.goide.GoTypes
import com.goide.inspections.core.GoProblemsHolder
import com.goide.psi.GoAssignmentStatement
import com.goide.psi.GoBlock
import com.goide.psi.GoCallExpr
import com.goide.psi.GoFunctionOrMethodDeclaration
import com.goide.psi.GoPointerType
import com.goide.psi.GoReferenceExpression
import com.goide.psi.GoVisitor
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.elementType
import io.vacuum.inspections.VacuumBaseLocalInspection
import io.vacuum.inspections.problems.VacuumInspectionMessage
import java.util.*

/**
 * @author Marcin Bukowiecki
 */
@Deprecated("Release in future")
class VacuumPassByValueInspection : VacuumBaseLocalInspection() {

    override fun buildGoVisitor(holder: GoProblemsHolder, session: LocalInspectionToolSession): GoVisitor {
        return object : GoVisitor() {

            private var functionContextStack: LinkedList<FunctionContext>? = null

            override fun visitElement(element: PsiElement) {
                if (element is LeafPsiElement &&
                    element.elementType == GoTypes.LBRACE &&
                    element.parent is GoBlock &&
                    element.parent.parent is GoFunctionOrMethodDeclaration) {

                    if (functionContextStack == null) {
                        functionContextStack = LinkedList()
                    }
                    functionContextStack?.addLast(FunctionContext())

                    val functionOrMethod: GoFunctionOrMethodDeclaration = (element.parent?.parent ?: return)
                            as GoFunctionOrMethodDeclaration

                    functionOrMethod.signature?.parameters?.parameterDeclarationList?.forEach { param ->
                        if (param.type is GoPointerType) {
                            functionContextStack?.last?.addParameter(param)
                        }
                    }

                } else if (element is LeafPsiElement &&
                    element.elementType == GoTypes.RBRACE &&
                    element.parent is GoBlock &&
                    element.parent.parent is GoFunctionOrMethodDeclaration) {

                    functionContextStack?.removeLast()?.let { lastCtx ->
                        handleLastContext(lastCtx, holder)
                    }
                }
            }

            override fun visitAssignmentStatement(assignmentStatement: GoAssignmentStatement) {
                if (!checkForParameterOverride(assignmentStatement)) {

                }
            }

            private fun checkForParameterOverride(assignmentStatement: GoAssignmentStatement): Boolean {
                if (isSingleAssign(assignmentStatement)) {
                    functionContextStack?.let { stack ->
                        if (stack.isNotEmpty()) {
                            return stack.last.removeIfExists(assignmentStatement.leftHandExprList.text)
                        }
                    }
                }
                return false
            }

            private fun checkForTypeFieldUpdate() {

            }

            override fun visitCallExpr(callExpr: GoCallExpr) {

            }
        }
    }

    private fun handleLastContext(ctx: FunctionContext, holder: GoProblemsHolder) {
        ctx.pointerParameters.forEach { paramPtr ->
            paramPtr.element?.let { elementToMark ->
                holder.registerProblem(elementToMark, VacuumInspectionMessage("Parameter is never updated. Change to pass by value."))
            }
        }
    }

    private fun isSingleAssign(assignmentStatement: GoAssignmentStatement): Boolean {
        return assignmentStatement.leftHandExprList.expressionList.size == 1 &&
                assignmentStatement.leftHandExprList.expressionList.first() is GoReferenceExpression &&
                assignmentStatement.leftHandExprList.expressionList.first().firstChild.elementType == GoTypes.IDENTIFIER
    }
}
