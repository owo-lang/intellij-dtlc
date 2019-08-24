package org.ice1000.tt.editing.mlpolyr

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.mlpolyr.MLPolyRTokenType
import org.ice1000.tt.psi.mlpolyr.MLPolyRTypes
import org.intellij.lang.annotations.Language

object MLPolyRHighlighter : MLPolyRGeneratedSyntaxHighlighter() {
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

	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		MLPolyRTypes.COMMA -> COMMA_KEY
		MLPolyRTypes.SEMI -> SEMICOLON_KEY
		MLPolyRTypes.ID -> IDENTIFIER_KEY
		MLPolyRTypes.DOT -> DOT_KEY
		MLPolyRTypes.STR -> STRING_KEY
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

class MLPolyRColorSettingsPage : MLPolyRGeneratedColorSettingsPage() {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.function-call"), MLPolyRHighlighter.FUNCTION_CALL),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.function-decl"), MLPolyRHighlighter.FUNCTION_DECL),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.parameter-call"), MLPolyRHighlighter.PARAMETER_CALL),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.parameter-decl"), MLPolyRHighlighter.PARAMETER_DECL),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.pattern-call"), MLPolyRHighlighter.PATTERN_CALL),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.pattern-decl"), MLPolyRHighlighter.PATTERN_DECL),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.variable-call"), MLPolyRHighlighter.VARIABLE_CALL),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.variable-decl"), MLPolyRHighlighter.VARIABLE_DECL),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.field-call"), MLPolyRHighlighter.FIELD_CALL),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.field-decl"), MLPolyRHighlighter.FIELD_DECL),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), MLPolyRHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.string"), MLPolyRHighlighter.STRING),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.number"), MLPolyRHighlighter.INT),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.identifier"), MLPolyRHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.semicolon"), MLPolyRHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comma"), MLPolyRHighlighter.COMMA),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.dot"), MLPolyRHighlighter.DOT),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.unresolved"), MLPolyRHighlighter.UNRESOLVED),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.constructor"), MLPolyRHighlighter.CONSTRUCTOR),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.operator"), MLPolyRHighlighter.OPERATOR),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.paren"), MLPolyRHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.bracket"), MLPolyRHighlighter.BRACK),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.brace"), MLPolyRHighlighter.BRACE),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.brace"), MLPolyRHighlighter.BRACE2),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comment"), MLPolyRHighlighter.COMMENT))

		private val ADDITIONAL_DESCRIPTORS = mapOf(
			"FC" to MLPolyRHighlighter.FUNCTION_CALL,
			"FD" to MLPolyRHighlighter.FUNCTION_DECL,
			"VC" to MLPolyRHighlighter.VARIABLE_CALL,
			"VD" to MLPolyRHighlighter.VARIABLE_DECL,
			"PC" to MLPolyRHighlighter.PARAMETER_CALL,
			"PD" to MLPolyRHighlighter.PARAMETER_DECL,
			"AC" to MLPolyRHighlighter.PATTERN_CALL,
			"AD" to MLPolyRHighlighter.PATTERN_DECL,
			"EC" to MLPolyRHighlighter.FIELD_CALL,
			"ED" to MLPolyRHighlighter.FIELD_DECL,
			"C" to MLPolyRHighlighter.CONSTRUCTOR,
			"Unresolved" to MLPolyRHighlighter.UNRESOLVED)
	}

	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	override fun getAttributeDescriptors() = DESCRIPTORS
	@Language("MLPolyR")
	override fun getDemoText() = """
let val <VD>n</VD> = { i := 1919810 }
    val <VD>m</VD> = {| j := 114514 |}
    fun <FD>withfresh</FD> <PD>f</PD> = let
        val <VD>i</VD> = <VC>n</VC>!<VC>i</VC> in <VC>n</VC>!<VC>i</VC> := <VC>i</VC>+1; <PC>f</PC> <VC>i</VC> end

    (* ---- utilities ---- *)
    fun <FD>Let</FD> <PD>{<ED>x</ED>, <ED>e1</ED>, <ED>e2</ED>}</PD> = <C>`App</C> (<C>`Lam</C> ([<EC>x</EC>], <EC>e2</EC>), [<EC>e1</EC>])
    fun <FD>kv2kb</FD> <PD>kv</PD> = fn <PD>v</PD> => <C>`App</C> (<PC>kv</PC>, [<PC>v</PC>])

    fun <FD>cvt_c</FD> <PD>(<AD>cvt</AD>, <AD>kb</AD>)</PD> =
    cases <C>`Const</C> <AD>i</AD> => <PC>kb</PC> (<C>`Const</C> <AC>i</AC>)
            | <C>`Var</C> <AD>x</AD> => <PC>kb</PC> (<C>`Var</C> <AC>x</AC>)
        | <C>`Lam</C> <PD>(<AD>xl</AD>, <AD>e</AD>)</PD> => <PC>kb</PC> (<Unresolved>cvt_lam</Unresolved> (<AC>cvt</AC>, <AC>xl</AC>, <AC>e</AC>))
        | <C>`App</C> <PD>(<AD>e</AD>, <AD>el</AD>)</PD> => <Unresolved>cvt_app</Unresolved> (<AC>cvt</AC>, <AC>e</AC>, <AC>el</AC>, <FC>kv2kb</FC> <PC>kb</PC>)
in
    String.output "24 years old student.";
    114514
end
"""
}
