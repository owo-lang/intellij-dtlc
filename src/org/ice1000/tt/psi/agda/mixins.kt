package org.ice1000.tt.psi.agda

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.GeneralNameIdentifier
import org.ice1000.tt.psi.agda.impl.AgdaDeclarationImpl
import org.ice1000.tt.psi.childrenRevWithLeaves

abstract class AgdaLayoutMixin(node: ASTNode) : ASTWrapperPsiElement(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) = childrenRevWithLeaves
		.filter { it is AgdaSignature || it is AgdaDataSignature }
		.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class AgdaNameDeclMixin(node: ASTNode) : GeneralNameIdentifier(node), AgdaNameDecl {
	override fun visit(visitor: (AgdaNameDecl) -> Boolean) = visitor(this)
}

abstract class AgdaImplementationMixin(node: ASTNode) : AgdaDeclarationImpl(node), AgdaImplementation {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		whereClause?.layout?.processDeclarations(processor, state, lastParent, place).orTrue()
}

abstract class AgdaDataMixin(node: ASTNode) : AgdaDeclarationImpl(node), AgdaData {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		whereSignatureClause?.layoutSignature?.processDeclarations(processor, state, lastParent, place).orTrue()
}

abstract class AgdaSignatureMixin(node: ASTNode) : AgdaDeclarationImpl(node), AgdaSignature {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) = nameDeclList
		.all { it.processDeclarations(processor, state, lastParent, place) }
}
