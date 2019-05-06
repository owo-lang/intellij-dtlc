package org.ice1000.tt.editing.mlpolyr

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.psi.mlpolyr.MLPolyRTokenType
import org.ice1000.tt.psi.mlpolyr.MLPolyRTypes
import org.ice1000.tt.psi.mlpolyr.mlpolyrLexer

object MLPolyRHighlighter : SyntaxHighlighter {
	@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("ML_POLY_R_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
	@JvmField val IDENTIFIER = TextAttributesKey.createTextAttributesKey("ML_POLY_R_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
	@JvmField val SEMICOLON = TextAttributesKey.createTextAttributesKey("ML_POLY_R_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
	@JvmField val COMMA = TextAttributesKey.createTextAttributesKey("ML_POLY_R_COMMA", DefaultLanguageHighlighterColors.COMMA)
	@JvmField val OPERATOR = TextAttributesKey.createTextAttributesKey("ML_POLY_R_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
	@JvmField val PAREN = TextAttributesKey.createTextAttributesKey("ML_POLY_R_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
	@JvmField val BRACK = TextAttributesKey.createTextAttributesKey("ML_POLY_R_BRACKET", DefaultLanguageHighlighterColors.BRACKETS)
	@JvmField val BRACE = TextAttributesKey.createTextAttributesKey("ML_POLY_R_BRACE", DefaultLanguageHighlighterColors.BRACES)
	@JvmField val BRACE2 = TextAttributesKey.createTextAttributesKey("ML_POLY_R_BRACE2", DefaultLanguageHighlighterColors.BRACES)
	@JvmField val COMMENT = TextAttributesKey.createTextAttributesKey("ML_POLY_R_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
	@JvmField val DOT = TextAttributesKey.createTextAttributesKey("ML_POLY_R_DOT", DefaultLanguageHighlighterColors.DOT)
	@JvmField val INT = TextAttributesKey.createTextAttributesKey("ML_POLY_R_INT", DefaultLanguageHighlighterColors.NUMBER)

	@JvmField val KEYWORD_KEY = arrayOf(KEYWORD)
	@JvmField val IDENTIFIER_KEY = arrayOf(IDENTIFIER)
	@JvmField val SEMICOLON_KEY = arrayOf(SEMICOLON)
	@JvmField val COMMA_KEY = arrayOf(COMMA)
	@JvmField val OPERATOR_KEY = arrayOf(OPERATOR)
	@JvmField val PAREN_KEY = arrayOf(PAREN)
	@JvmField val BRACK_KEY = arrayOf(BRACK)
	@JvmField val BRACE_KEY = arrayOf(BRACE)
	@JvmField val BRACE2_KEY = arrayOf(BRACE2)
	@JvmField val COMMENT_KEY = arrayOf(COMMENT)
	@JvmField val DOT_KEY = arrayOf(DOT)
	@JvmField val INT_KEY = arrayOf(INT)

	private val KEYWORDS_LIST = listOf(
		MLPolyRTypes.KW_REHANDLING,
		MLPolyRTypes.KW_HANDLING,
		MLPolyRTypes.KW_DEFAULT,
		MLPolyRTypes.KW_NOCASES,
		MLPolyRTypes.KW_ORELSE,
		MLPolyRTypes.KW_ISNULL,
		MLPolyRTypes.KW_FALSE,
		MLPolyRTypes.KW_MATCH,
		MLPolyRTypes.KW_CASES,
		MLPolyRTypes.KW_WHERE,
		MLPolyRTypes.KW_RAISE,
		MLPolyRTypes.KW_THEN,
		MLPolyRTypes.KW_ELSE,
		MLPolyRTypes.KW_TRUE,
		MLPolyRTypes.KW_WITH,
		MLPolyRTypes.KW_CASE,
		MLPolyRTypes.KW_LET,
		MLPolyRTypes.KW_END,
		MLPolyRTypes.KW_FUN,
		MLPolyRTypes.KW_AND,
		MLPolyRTypes.KW_VAL,
		MLPolyRTypes.KW_TRY,
		MLPolyRTypes.KW_NOT,
		MLPolyRTypes.KW_IF,
		MLPolyRTypes.KW_FN,
		MLPolyRTypes.KW_AS,
		MLPolyRTypes.KW_OF,
		MLPolyRTypes.KW_IN)

	private val OPERATORS_LIST = listOf(
		MLPolyRTypes.LTEQ,
		MLPolyRTypes.DEQ,
		MLPolyRTypes.NEQ,
		MLPolyRTypes.DCOLON,
		MLPolyRTypes.ASSIGN,
		MLPolyRTypes.DARROW,
		MLPolyRTypes.GTEQ,
		MLPolyRTypes.DOTDOTDOT,
		MLPolyRTypes.EXCLAM,
		MLPolyRTypes.TIMES,
		MLPolyRTypes.PLUS,
		MLPolyRTypes.MOD,
		MLPolyRTypes.DIV,
		MLPolyRTypes.BAR,
		MLPolyRTypes.EQ,
		MLPolyRTypes.LT,
		MLPolyRTypes.GT,
		MLPolyRTypes.MINUS,
		MLPolyRTypes.PLUSPLUS
	)

	override fun getHighlightingLexer() = mlpolyrLexer()
	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		MLPolyRTypes.COMMA -> COMMA_KEY
		MLPolyRTypes.SEMI -> SEMICOLON_KEY
		MLPolyRTypes.IDENTIFIER -> IDENTIFIER_KEY
		MLPolyRTypes.DOT -> DOT_KEY
		MLPolyRTypes.INT -> INT_KEY
		MLPolyRTypes.LP, MLPolyRTypes.RP -> PAREN_KEY
		MLPolyRTypes.LSB, MLPolyRTypes.RSB -> BRACK_KEY
		MLPolyRTypes.LCB, MLPolyRTypes.RCB -> BRACE_KEY
		MLPolyRTypes.LCBB, MLPolyRTypes.LCBB -> BRACE2_KEY
		in OPERATORS_LIST -> OPERATOR_KEY
		in KEYWORDS_LIST -> KEYWORD_KEY
		in MLPolyRTokenType.COMMENTS -> COMMENT_KEY
		else -> emptyArray()
	}
}

class MLPolyRHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = MLPolyRHighlighter
}
