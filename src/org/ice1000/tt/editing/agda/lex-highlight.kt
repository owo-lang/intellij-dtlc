package org.ice1000.tt.editing.agda

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.psi.agda.AgdaTokenType
import org.ice1000.tt.psi.agda.AgdaTypes
import org.ice1000.tt.psi.agda.agdaLexer

object AgdaHighlighter : SyntaxHighlighter {
	@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("AGDA_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
	@JvmField val IDENTIFIER = TextAttributesKey.createTextAttributesKey("AGDA_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
	@JvmField val SEMICOLON = TextAttributesKey.createTextAttributesKey("AGDA_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
	@JvmField val DOT = TextAttributesKey.createTextAttributesKey("AGDA_DOT", DefaultLanguageHighlighterColors.DOT)
	@JvmField val LINE_COMMENT = TextAttributesKey.createTextAttributesKey("AGDA_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
	@JvmField val BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey("AGDA_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
	@JvmField val NUMBER = TextAttributesKey.createTextAttributesKey("AGDA_NUMERAL", DefaultLanguageHighlighterColors.NUMBER)
	@JvmField val FLOAT = TextAttributesKey.createTextAttributesKey("AGDA_NUMERAL", DefaultLanguageHighlighterColors.NUMBER)

	@JvmField val KEYWORD_KEY = arrayOf(KEYWORD)
	@JvmField val IDENTIFIER_KEY = arrayOf(IDENTIFIER)
	@JvmField val SEMICOLON_KEY = arrayOf(SEMICOLON)
	@JvmField val DOT_KEY = arrayOf(DOT)
	@JvmField val LINE_COMMENT_KEY = arrayOf(LINE_COMMENT)
	@JvmField val BLOCK_COMMENT_KEY = arrayOf(BLOCK_COMMENT)
	@JvmField val NUMBER_KEY = arrayOf(NUMBER)
	@JvmField val FLOAT_KEY = arrayOf(FLOAT)

	override fun getHighlightingLexer() = agdaLexer()
	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		AgdaTypes.KEYWORD -> KEYWORD_KEY
		AgdaTypes.IDENTIFIER -> IDENTIFIER_KEY
		AgdaTypes.SEMI -> SEMICOLON_KEY
		AgdaTypes.FLOAT -> FLOAT_KEY
		AgdaTypes.NUMBER -> NUMBER_KEY
		AgdaTypes.DOT, AgdaTypes.DOT_DOT, AgdaTypes.ELLIPSIS -> DOT_KEY
		AgdaTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		AgdaTokenType.BLOCK_COMMENT -> BLOCK_COMMENT_KEY
		else -> emptyArray()
	}
}

class AgdaHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = AgdaHighlighter
}
