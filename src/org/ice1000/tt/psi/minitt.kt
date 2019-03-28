package org.ice1000.tt.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.MiniTTLanguage

class MiniTTElementType(debugName: String) : IElementType(debugName, MiniTTLanguage.INSTANCE)

class MiniTTTokenType(debugName: String) : IElementType(debugName, MiniTTLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = MiniTTTokenType("line comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT)
		@JvmField val WHITE_SPACE = TokenSet.create(TokenType.WHITE_SPACE)
		@JvmField val IDENTIFIERS = TokenSet.create(MiniTTTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(MiniTTLanguage.INSTANCE, text).firstChild
	}
}
