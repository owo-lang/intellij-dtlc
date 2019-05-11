package org.ice1000.tt.editing.redprl

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.psi.redprl.RedPrlTokenType
import org.ice1000.tt.psi.redprl.RedPrlTypes
import org.ice1000.tt.psi.redprl.redPrlLexer

object RedPrlHighlighter : SyntaxHighlighter {
	@JvmField val PAREN = TextAttributesKey.createTextAttributesKey("RED_PRL_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
	@JvmField val BRACK = TextAttributesKey.createTextAttributesKey("RED_PRL_BRACKET", DefaultLanguageHighlighterColors.BRACKETS)
	@JvmField val BRACE = TextAttributesKey.createTextAttributesKey("RED_PRL_BRACE", DefaultLanguageHighlighterColors.BRACES)
	@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("RED_PRL_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
	@JvmField val OP_NAME_DECL = TextAttributesKey.createTextAttributesKey("RED_PRL_OP_DECL", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE)
	@JvmField val OP_NAME_CALL = TextAttributesKey.createTextAttributesKey("RED_PRL_OP_CALL", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE)
	@JvmField val VAR_NAME_DECL = TextAttributesKey.createTextAttributesKey("RED_PRL_VAR_DECL", DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
	@JvmField val VAR_NAME_CALL = TextAttributesKey.createTextAttributesKey("RED_PRL_VAR_CALL", DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
	@JvmField val SEMICOLON = TextAttributesKey.createTextAttributesKey("RED_PRL_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
	@JvmField val COMMA = TextAttributesKey.createTextAttributesKey("RED_PRL_COMMA", DefaultLanguageHighlighterColors.COMMA)
	@JvmField val DOT = TextAttributesKey.createTextAttributesKey("RED_PRL_DOT", DefaultLanguageHighlighterColors.DOT)
	@JvmField val OPERATOR = TextAttributesKey.createTextAttributesKey("RED_PRL_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
	@JvmField val LINE_COMMENT = TextAttributesKey.createTextAttributesKey("RED_PRL_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
	@JvmField val BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey("RED_PRL_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
	@JvmField val NUMERAL = TextAttributesKey.createTextAttributesKey("RED_PRL_NUMERAL", DefaultLanguageHighlighterColors.NUMBER)
	@JvmField val HASH = TextAttributesKey.createTextAttributesKey("RED_PRL_HASH", DefaultLanguageHighlighterColors.METADATA)
	@JvmField val HOLE = TextAttributesKey.createTextAttributesKey("RED_PRL_HOLE", DefaultLanguageHighlighterColors.LABEL)

	@JvmField val PAREN_KEY = arrayOf(PAREN)
	@JvmField val BRACK_KEY = arrayOf(BRACK)
	@JvmField val BRACE_KEY = arrayOf(BRACE)
	@JvmField val KEYWORD_KEY = arrayOf(KEYWORD)
	@JvmField val OP_NAME_CALL_KEY = arrayOf(OP_NAME_CALL)
	@JvmField val VAR_NAME_CALL_KEY = arrayOf(VAR_NAME_CALL)
	@JvmField val SEMICOLON_KEY = arrayOf(SEMICOLON)
	@JvmField val COMMA_KEY = arrayOf(COMMA)
	@JvmField val DOT_KEY = arrayOf(DOT)
	@JvmField val OPERATOR_KEY = arrayOf(OPERATOR)
	@JvmField val LINE_COMMENT_KEY = arrayOf(LINE_COMMENT)
	@JvmField val BLOCK_COMMENT_KEY = arrayOf(BLOCK_COMMENT)
	@JvmField val NUMERAL_KEY = arrayOf(NUMERAL)
	@JvmField val HASH_KEY = arrayOf(HASH)
	@JvmField val HOLE_KEY = arrayOf(HOLE)

	@JvmField val OPERATORS = listOf(
		RedPrlTypes.RANGLE,
		RedPrlTypes.DOT,
		RedPrlTypes.COLON,
		RedPrlTypes.EQUALS,
		RedPrlTypes.RIGHT_ARROW,
		RedPrlTypes.LEFT_ARROW,
		RedPrlTypes.SQUIGGLE_RIGHT_ARROW,
		RedPrlTypes.SQUIGGLE_LEFT_ARROW,
		RedPrlTypes.DOUBLE_RIGHT_ARROW,
		RedPrlTypes.BACK_TICK,
		RedPrlTypes.TIMES,
		RedPrlTypes.BANG,
		RedPrlTypes.AT_SIGN,
		RedPrlTypes.DOLLAR_SIGN,
		RedPrlTypes.DOUBLE_PIPE,
		RedPrlTypes.PIPE,
		RedPrlTypes.PERCENT,
		RedPrlTypes.UNDER,
		RedPrlTypes.PLUS,
		RedPrlTypes.DOUBLE_PLUS)

	// Regular expression used:
	// s/[a-zA-Z\-]+ \{ return ([A-Z_]+); }/RedPrlTypes.$1,/rg
	@JvmField val KEYWORDS = listOf(
		RedPrlTypes.PUSHOUT_REC,
		RedPrlTypes.TAC_ASSUMPTION,
		RedPrlTypes.TAC_AUTO_STEP,
		RedPrlTypes.TAC_INVERSION,
		RedPrlTypes.COEQUALIZER_REC,
		RedPrlTypes.MTAC_PROGRESS,
		RedPrlTypes.TAC_SYMMETRY,
		RedPrlTypes.DISCRETE,
		RedPrlTypes.NAT_REC,
		RedPrlTypes.NEGSUCC,
		RedPrlTypes.INT_REC,
		RedPrlTypes.PUSHOUT,
		RedPrlTypes.WITHOUT,
		RedPrlTypes.EXTRACT,
		RedPrlTypes.THEOREM,
		RedPrlTypes.TAC_REWRITE,
		RedPrlTypes.RECORD,
		RedPrlTypes.REFINE,
		RedPrlTypes.DEFINE,
		RedPrlTypes.TACTIC_KW,
		RedPrlTypes.MTAC_REPEAT,
		RedPrlTypes.TAC_REDUCE,
		RedPrlTypes.TAC_UNFOLD,
		RedPrlTypes.TUPLE,
		RedPrlTypes.RIGHT,
		RedPrlTypes.CECOD,
		RedPrlTypes.CEDOM,
		RedPrlTypes.VPROJ,
		RedPrlTypes.CLAIM,
		RedPrlTypes.RULE_EXACT,
		RedPrlTypes.MATCH,
		RedPrlTypes.QUERY,
		RedPrlTypes.CONCL,
		RedPrlTypes.PRINT,
		RedPrlTypes.FCOM,
		RedPrlTypes.BOOL,
		RedPrlTypes.ZERO,
		RedPrlTypes.SUCC,
		RedPrlTypes.VOID,
		RedPrlTypes.BASE,
		RedPrlTypes.LOOP,
		RedPrlTypes.PATH,
		RedPrlTypes.LINE,
		RedPrlTypes.LEFT,
		RedPrlTypes.GLUE,
		RedPrlTypes.COEQUALIZER,
		RedPrlTypes.SELF,
		RedPrlTypes.ECOM,
		RedPrlTypes.HCOM,
		RedPrlTypes.THEN,
		RedPrlTypes.ELSE,
		RedPrlTypes.WITH,
		RedPrlTypes.CASE,
		RedPrlTypes.LMAX,
		RedPrlTypes.QUIT,
		RedPrlTypes.DATA,
		RedPrlTypes.TAC_FAIL,
		RedPrlTypes.MTAC_AUTO,
		RedPrlTypes.TAC_ELIM,
		RedPrlTypes.TRUE_KW,
		RedPrlTypes.TYPE,
		RedPrlTypes.NAT,
		RedPrlTypes.INT,
		RedPrlTypes.POS,
		RedPrlTypes.LAMBDA,
		RedPrlTypes.REC,
		RedPrlTypes.MEM,
		RedPrlTypes.BOX,
		RedPrlTypes.CAP,
		RedPrlTypes.VIN,
		RedPrlTypes.ABS,
		RedPrlTypes.COM,
		RedPrlTypes.COE,
		RedPrlTypes.LET,
		RedPrlTypes.USE,
		RedPrlTypes.DIM,
		RedPrlTypes.LVL,
		RedPrlTypes.KND,
		RedPrlTypes.EXP,
		RedPrlTypes.TAC_KW,
		RedPrlTypes.JDG,
		RedPrlTypes.VAL,
		RedPrlTypes.KAN,
		RedPrlTypes.PRE,
		RedPrlTypes.AX,
		RedPrlTypes.TT,
		RedPrlTypes.FF,
		RedPrlTypes.IF,
		RedPrlTypes.MEM,
		RedPrlTypes.OF,
		RedPrlTypes.BY,
		RedPrlTypes.IN,
		RedPrlTypes.FN,
		RedPrlTypes.TAC_ID,
		RedPrlTypes.AT,
		RedPrlTypes.V,
		RedPrlTypes.UNIVERSE)

	override fun getHighlightingLexer() = redPrlLexer()
	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		RedPrlTypes.COMMA -> COMMA_KEY
		RedPrlTypes.HASH -> HASH_KEY
		RedPrlTypes.VARNAME -> VAR_NAME_CALL_KEY
		RedPrlTypes.HOLENAME -> HOLE_KEY
		RedPrlTypes.OPNAME -> OP_NAME_CALL_KEY
		RedPrlTypes.SEMI -> SEMICOLON_KEY
		RedPrlTypes.DOT -> DOT_KEY
		RedPrlTypes.NUMERAL -> NUMERAL_KEY
		RedPrlTypes.LSQUARE, RedPrlTypes.RSQUARE -> BRACK_KEY
		RedPrlTypes.LPAREN, RedPrlTypes.RPAREN -> PAREN_KEY
		RedPrlTypes.LBRACKET, RedPrlTypes.RBRACKET -> BRACE_KEY
		RedPrlTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		RedPrlTokenType.BLOCK_COMMENT -> BLOCK_COMMENT_KEY
		in OPERATORS -> OPERATOR_KEY
		in KEYWORDS -> KEYWORD_KEY
		else -> emptyArray()
	}
}

class RedPrlHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = RedPrlHighlighter
}
