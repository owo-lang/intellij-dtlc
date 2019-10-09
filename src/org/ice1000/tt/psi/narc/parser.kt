package org.ice1000.tt.psi.narc

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.NarcLanguage

class NarcTokenType(debugName: String) : IElementType(debugName, NarcLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = NarcTokenType("line comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(NarcTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(NarcLanguage.INSTANCE, text).firstChild
		fun createDef(text: String, project: Project) = fromText(text, project) as? NarcDefinition
	}
}

