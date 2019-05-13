package org.ice1000.tt.editing.agda

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.psi.agda.AgdaTokenType
import org.ice1000.tt.psi.agda.agdaLexer

object AgdaHighlighter : SyntaxHighlighter {
	@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("ML_POLY_R_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
	@JvmField val IDENTIFIER = TextAttributesKey.createTextAttributesKey("ML_POLY_R_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
	@JvmField val KEYWORD_KEY = arrayOf(KEYWORD)
	@JvmField val IDENTIFIER_KEY = arrayOf(IDENTIFIER)

	override fun getHighlightingLexer() = agdaLexer()
	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		AgdaTokenType.KEYWORD -> KEYWORD_KEY
		AgdaTokenType.IDENTIFIER -> IDENTIFIER_KEY
		else -> emptyArray()
	}
}

class AgdaHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = AgdaHighlighter
}
