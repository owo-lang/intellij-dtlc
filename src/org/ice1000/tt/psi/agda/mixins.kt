package org.ice1000.tt.psi.agda

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import org.ice1000.tt.psi.childrenRevWithLeaves

abstract class AgdaLayoutMixin(node: ASTNode) : ASTWrapperPsiElement(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) = childrenRevWithLeaves
		.filterIsInstance<AgdaSignature>()
		.all { it.processDeclarations(processor, state, lastParent, place) }
}
