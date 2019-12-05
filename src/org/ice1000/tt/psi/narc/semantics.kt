package org.ice1000.tt.psi.narc

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import org.ice1000.tt.psi.GeneralReference

abstract class NarcDataMixin(node: ASTNode) : NarcDataGeneratedMixin(node), NarcData {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		constructorList.all {
			it.processDeclarations(processor, state, lastParent, place)
		} && super.processDeclarations(processor, state, lastParent, place)
}

abstract class NarcCodataMixin(node: ASTNode) : NarcCodataGeneratedMixin(node), NarcCodata {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		projectionList.all {
			it.processDeclarations(processor, state, lastParent, place)
		} && super.processDeclarations(processor, state, lastParent, place)
}

abstract class NarcNameUsageMixin(node: ASTNode) : GeneralReference(node), NarcNameUsage {
}
