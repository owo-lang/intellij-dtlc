package org.ice1000.tt.editing.cubicaltt

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.psi.cubicaltt.CubicalTTTokenType
import org.ice1000.tt.psi.cubicaltt.CubicalTTTypes
import org.ice1000.tt.psi.cubicaltt.cubicalTTLexer

object CubicalTTHighlighter : SyntaxHighlighter {
	@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("CUBICAL_TT_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
	@JvmField val IDENTIFIER = TextAttributesKey.createTextAttributesKey("CUBICAL_TT_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
	@JvmField val SEMICOLON = TextAttributesKey.createTextAttributesKey("CUBICAL_TT_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
	@JvmField val COMMA = TextAttributesKey.createTextAttributesKey("CUBICAL_TT_COMMA", DefaultLanguageHighlighterColors.COMMA)
	@JvmField val PAREN = TextAttributesKey.createTextAttributesKey("CUBICAL_TT_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
	@JvmField val BRACK = TextAttributesKey.createTextAttributesKey("CUBICAL_TT_BRACK", DefaultLanguageHighlighterColors.BRACKETS)
	@JvmField val LINE_COMMENT = TextAttributesKey.createTextAttributesKey("CUBICAL_TT_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
	@JvmField val BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey("CUBICAL_TT_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)

	@JvmField val KEYWORD_KEY = arrayOf(KEYWORD)
	@JvmField val IDENTIFIER_KEY = arrayOf(IDENTIFIER)
	@JvmField val SEMICOLON_KEY = arrayOf(SEMICOLON)
	@JvmField val COMMA_KEY = arrayOf(COMMA)
	@JvmField val PAREN_KEY = arrayOf(PAREN)
	@JvmField val BRACK_KEY = arrayOf(BRACK)
	@JvmField val LINE_COMMENT_KEY = arrayOf(LINE_COMMENT)
	@JvmField val BLOCK_COMMENT_KEY = arrayOf(BLOCK_COMMENT)

	private val KEYWORDS = listOf(
		CubicalTTTypes.KW_TRANSPARENT_ALL,
		CubicalTTTypes.KW_TRANSPARENT,
		CubicalTTTypes.KW_TRANSPORT,
		CubicalTTTypes.KW_UNDEFINED,
		CubicalTTTypes.KW_IMPORT,
		CubicalTTTypes.KW_MODULE,
		CubicalTTTypes.KW_MUTUAL,
		CubicalTTTypes.KW_OPAQUE,
		CubicalTTTypes.KW_SPLIT_AT,
		CubicalTTTypes.KW_UNGLUE,
		CubicalTTTypes.KW_HCOMP,
		CubicalTTTypes.KW_HDATA,
		CubicalTTTypes.KW_PATH_P,
		CubicalTTTypes.KW_SPLIT,
		CubicalTTTypes.KW_WHERE,
		CubicalTTTypes.KW_COMP,
		CubicalTTTypes.KW_DATA,
		CubicalTTTypes.KW_FILL,
		CubicalTTTypes.KW_GLUE,
		CubicalTTTypes.KW_GLUE2,
		CubicalTTTypes.KW_WITH,
		CubicalTTTypes.KW_IDC,
		CubicalTTTypes.KW_IDJ,
		CubicalTTTypes.KW_LET,
		CubicalTTTypes.KW_ID,
		CubicalTTTypes.KW_IN,
		CubicalTTTypes.KW_U
	)

	override fun getHighlightingLexer() = cubicalTTLexer()
	override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> = when (tokenType) {
		CubicalTTTypes.IDENTIFIER -> IDENTIFIER_KEY
		CubicalTTTypes.LAYOUT_SEP -> SEMICOLON_KEY
		CubicalTTTypes.LPAREN, CubicalTTTypes.RPAREN -> PAREN_KEY
		CubicalTTTypes.LBRACK, CubicalTTTypes.RBRACK -> BRACK_KEY
		CubicalTTTypes.COMMA -> COMMA_KEY
		CubicalTTTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		CubicalTTTokenType.BLOCK_COMMENT -> BLOCK_COMMENT_KEY
		in KEYWORDS -> KEYWORD_KEY
		else -> emptyArray()
	}
}

class CubicalTTHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = CubicalTTHighlighter
}

