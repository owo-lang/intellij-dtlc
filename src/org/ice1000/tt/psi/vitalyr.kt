@file:Suppress("PackageDirectoryMismatch")

package org.ice1000.tt.psi.vitalyr

import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import icons.SemanticIcons
import org.ice1000.tt.VitalyRLanguage
import org.ice1000.tt.psi.GeneralNameIdentifier
import org.ice1000.tt.psi.invalidName

class VitalyRTokenType(debugName: String) : IElementType(debugName, VitalyRLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = VitalyRTokenType("line comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(VitalyRTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(VitalyRLanguage.INSTANCE, text).firstChild
		fun createLambda(text: String, project: Project) = fromText(text, project) as? VitalyRLambda
		fun createNameDecl(text: String, project: Project) = createLambda("lambda $text = e", project)?.nameDecl
		fun createExpr(text: String, project: Project) = createLambda("lambda x = $text", project)?.expr
		fun createNameUsage(text: String, project: Project) = createExpr(text, project) as? VitalyRNameUsage
	}
}

abstract class VitalyRNameDeclMixin(node: ASTNode) : GeneralNameIdentifier(node), VitalyRNameDecl {
	override fun getIcon(flags: Int) = SemanticIcons.BLUE_HOLE
	override fun setName(newName: String): PsiElement = replace(
		VitalyRTokenType.createNameDecl(newName, project) ?: invalidName(newName))
}
