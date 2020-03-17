package org.ice1000.tt.psi.miniagda

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.MiniAgdaLanguage

class MiniAgdaTokenType(debugName: String) : IElementType(debugName, MiniAgdaLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = MiniAgdaTokenType("line comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(MiniAgdaTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(MiniAgdaLanguage.INSTANCE, text).firstChild
	}
}
