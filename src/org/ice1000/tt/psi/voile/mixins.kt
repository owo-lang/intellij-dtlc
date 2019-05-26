package org.ice1000.tt.psi.voile

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.TokenType
import com.intellij.psi.scope.PsiScopeProcessor
import icons.TTIcons
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.*

abstract class VoileNameDeclMixin(node: ASTNode) : GeneralNameIdentifier(node), VoileNameDecl {
	override fun getIcon(flags: Int) = TTIcons.VOILE
	override fun setName(newName: String): PsiElement = replace(
		VoileTokenType.createNameDecl(text, project) ?: invalidName(newName))
}

abstract class VoileGlobDeclMixin(node: ASTNode) : GeneralDeclaration(node) {
	override val type: PsiElement? get() = findChildByType<PsiElement>(VoileTypes.COLON)?.nextSiblingIgnoring(TokenType.WHITE_SPACE)
	override fun getNameIdentifier(): PsiElement? = findChildByClass(VoileNameDeclMixin::class.java)
	override fun setName(newName: String): PsiElement {
		nameIdentifier?.replace(
			VoileTokenType.createNameDecl(text, project) ?: invalidName(newName))
		return this
	}
}

abstract class VoileLocalDeclMixin(node: ASTNode) : VoileGlobDeclMixin(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		childrenRevWithLeaves.filterIsInstance<VoileNameDeclMixin>().all { it.processDeclarations(processor, state, lastParent, place) }
	override fun setName(newName: String): PsiElement = invalidName(newName)
}

abstract class VoilePiSigMixin(node: ASTNode) : ASTWrapperPsiElement(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		firstChild?.processDeclarations(processor, state, lastParent, place).orTrue()
}
