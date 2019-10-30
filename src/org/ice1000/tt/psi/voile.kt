@file:Suppress("PackageDirectoryMismatch")

package org.ice1000.tt.psi.voile

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import icons.SemanticIcons
import org.ice1000.tt.VoileLanguage
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.GeneralNameIdentifier
import org.ice1000.tt.psi.childrenRevWithLeaves
import org.ice1000.tt.psi.invalidName

class VoileTokenType(debugName: String) : IElementType(debugName, VoileLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = VoileTokenType("line comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(VoileTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(VoileLanguage.INSTANCE, text).firstChild
		fun createImpl(text: String, project: Project) = fromText(text, project) as? VoileImplementation
		fun createNameDecl(text: String, project: Project) = createImpl("let $text = a;", project)?.nameDecl
		fun createExpr(text: String, project: Project) = createImpl("let n = $text;", project)?.expr
		fun createNameUsage(text: String, project: Project) = createExpr(text, project) as? VoileNameUsage
	}
}

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
