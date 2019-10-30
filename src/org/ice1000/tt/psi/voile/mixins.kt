package org.ice1000.tt.psi.voile

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import icons.SemanticIcons
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.GeneralNameIdentifier
import org.ice1000.tt.psi.childrenRevWithLeaves
import org.ice1000.tt.psi.invalidName

abstract class VoileNameDeclMixin(node: ASTNode) : GeneralNameIdentifier(node), VoileNameDecl {
	override fun getIcon(flags: Int) = SemanticIcons.BLUE_HOLE
	override fun setName(newName: String): PsiElement = replace(
		VoileTokenType.createNameDecl(newName, project) ?: invalidName(newName))
}

abstract class VoileLocalDeclMixin(node: ASTNode) : VoileGlobDeclGeneratedMixin(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		childrenRevWithLeaves.all { it.processDeclarations(processor, state, lastParent, place) }
	override fun getIcon(flags: Int) = SemanticIcons.PURPLE_P
	override fun setName(newName: String): PsiElement = invalidName(newName)
}

abstract class VoilePiSigMixin(node: ASTNode) : ASTWrapperPsiElement(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		firstChild?.processDeclarations(processor, state, lastParent, place).orTrue()
}
