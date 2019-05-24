package org.ice1000.tt.psi.acore

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.ACoreLanguage

class ACoreTokenType(debugName: String) : IElementType(debugName, ACoreLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = ACoreTokenType("line comment")
		@JvmField val BLOCK_COMMENT = ACoreTokenType("block comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(ACoreTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(ACoreLanguage.INSTANCE, text).firstChild
		fun createDeclaration(text: String, project: Project) = fromText(text, project) as? ACoreDeclarationExpression
		fun createPattern(text: String, project: Project) = createDeclaration("let $text:U = 0;", project)?.declaration?.pattern
		fun createAtomPattern(text: String, project: Project) = createPattern(text, project) as? ACoreAtomPattern
		fun createVariable(text: String, project: Project) = createAtomPattern(text, project)?.variable
	}
}
