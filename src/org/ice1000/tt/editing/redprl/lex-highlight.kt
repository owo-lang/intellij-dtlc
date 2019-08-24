package org.ice1000.tt.editing.redprl

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.redprl.RedPrlTokenType
import org.ice1000.tt.psi.redprl.RedPrlTypes
import org.intellij.lang.annotations.Language

object RedPrlHighlighter : RedPrlGeneratedSyntaxHighlighter() {
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

class RedPrlColorSettingsPage : RedPrlGeneratedColorSettingsPage() {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor(TTBundle.message("redprl.highlighter.settings.meta-decl"), RedPrlHighlighter.META_VAR_DECL),
			AttributesDescriptor(TTBundle.message("redprl.highlighter.settings.meta-call"), RedPrlHighlighter.META_VAR_CALL),
			AttributesDescriptor(TTBundle.message("redprl.highlighter.settings.var-decl"), RedPrlHighlighter.VAR_NAME_DECL),
			AttributesDescriptor(TTBundle.message("redprl.highlighter.settings.var-call"), RedPrlHighlighter.VAR_NAME_CALL),
			AttributesDescriptor(TTBundle.message("redprl.highlighter.settings.op-decl"), RedPrlHighlighter.OP_NAME_DECL),
			AttributesDescriptor(TTBundle.message("redprl.highlighter.settings.op-call"), RedPrlHighlighter.OP_NAME_CALL),
			AttributesDescriptor(TTBundle.message("redprl.highlighter.settings.hash"), RedPrlHighlighter.HASH),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), RedPrlHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.number"), RedPrlHighlighter.NUMERAL),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.hole"), RedPrlHighlighter.HOLE),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.semicolon"), RedPrlHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comma"), RedPrlHighlighter.COMMA),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.dot"), RedPrlHighlighter.DOT),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.operator"), RedPrlHighlighter.OPERATOR),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.paren"), RedPrlHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.bracket"), RedPrlHighlighter.BRACK),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.brace"), RedPrlHighlighter.BRACE),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.line-comment"), RedPrlHighlighter.LINE_COMMENT),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.block-comment"), RedPrlHighlighter.BLOCK_COMMENT))

		private val ADDITIONAL_DESCRIPTORS = mapOf(
			"MD" to RedPrlHighlighter.META_VAR_DECL,
			"MC" to RedPrlHighlighter.META_VAR_CALL,
			"VD" to RedPrlHighlighter.VAR_NAME_DECL,
			"VC" to RedPrlHighlighter.VAR_NAME_CALL,
			"OD" to RedPrlHighlighter.OP_NAME_DECL,
			"OC" to RedPrlHighlighter.OP_NAME_CALL,
			"H" to RedPrlHighlighter.HOLE)
	}

	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	override fun getAttributeDescriptors() = DESCRIPTORS
	@Language("HTML")
	override fun getDemoText() = """
// J rule definition
theorem <OD>J</OD>(<MD>#l</MD>: lvl) :
  (->
   [<VD>ty</VD> : (U <MC>#l</MC> kan)]
   /* ignored some trivial parts */
   (${'$'} fam x p))
by {
  <H>?check-this-out</H>;
  lam <VD>ty</VD> <VD>a</VD> <VD>fam</VD> <VD>d</VD> <VD>x</VD> <VD>p</VD> =>
     `(<OC>J/coe</OC> (dim 1) <VC>ty</VC> <VC>a</VC> <VC>fam</VC> <VC>d</VC> <VC>p</VC>)
}.
"""
}
