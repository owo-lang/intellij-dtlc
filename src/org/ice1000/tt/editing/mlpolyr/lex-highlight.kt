package org.ice1000.tt.editing.mlpolyr

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import icons.TTIcons
import org.ice1000.tt.MLPolyRFileType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.mlpolyr.MLPolyRTokenType
import org.ice1000.tt.psi.mlpolyr.MLPolyRTypes
import org.ice1000.tt.psi.mlpolyr.mlpolyrLexer
import org.intellij.lang.annotations.Language

object MLPolyRHighlighter : SyntaxHighlighter {
	@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("ML_POLY_R_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
	@JvmField val IDENTIFIER = TextAttributesKey.createTextAttributesKey("ML_POLY_R_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
	@JvmField val UNRESOLVED = TextAttributesKey.createTextAttributesKey("ML_POLY_R_UNRESOLVED", HighlighterColors.BAD_CHARACTER)
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
	@JvmField val STRING = TextAttributesKey.createTextAttributesKey("ML_POLY_R_STRING", DefaultLanguageHighlighterColors.STRING)

	@JvmField val FUNCTION_CALL = TextAttributesKey.createTextAttributesKey("ML_POLY_R_FUNCTION_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL)
	@JvmField val FUNCTION_DECL = TextAttributesKey.createTextAttributesKey("ML_POLY_R_FUNCTION_DECL", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
	@JvmField val VARIABLE_CALL = TextAttributesKey.createTextAttributesKey("ML_POLY_R_VARIABLE_CALL", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE)
	@JvmField val VARIABLE_DECL = TextAttributesKey.createTextAttributesKey("ML_POLY_R_VARIABLE_DECL", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE)
	@JvmField val PARAMETER_CALL = TextAttributesKey.createTextAttributesKey("ML_POLY_R_PARAMETER_CALL", DefaultLanguageHighlighterColors.PARAMETER)
	@JvmField val PARAMETER_DECL = TextAttributesKey.createTextAttributesKey("ML_POLY_R_PARAMETER_DECL", DefaultLanguageHighlighterColors.PARAMETER)
	@JvmField val CONSTRUCTOR = TextAttributesKey.createTextAttributesKey("ML_POLY_R_CONSTRUCTOR", DefaultLanguageHighlighterColors.LABEL)

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
	@JvmField val STRING_KEY = arrayOf(STRING)

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

class MLPolyRHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = MLPolyRHighlighter
}

class MLPolyRColorSettingsPage : ColorSettingsPage {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.function-call"), MLPolyRHighlighter.FUNCTION_CALL),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.function-decl"), MLPolyRHighlighter.FUNCTION_DECL),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.parameter-call"), MLPolyRHighlighter.PARAMETER_CALL),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.parameter-decl"), MLPolyRHighlighter.PARAMETER_DECL),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.variable-call"), MLPolyRHighlighter.VARIABLE_CALL),
			AttributesDescriptor(TTBundle.message("mlpolyr.highlighter.settings.variable-decl"), MLPolyRHighlighter.VARIABLE_DECL),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), MLPolyRHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.identifier"), MLPolyRHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.semicolon"), MLPolyRHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comma"), MLPolyRHighlighter.COMMA),
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
			"C" to MLPolyRHighlighter.CONSTRUCTOR,
			"Unresolved" to MLPolyRHighlighter.UNRESOLVED)
	}

	override fun getHighlighter(): SyntaxHighlighter = MLPolyRHighlighter
	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	override fun getIcon() = TTIcons.MLPOLYR
	override fun getAttributeDescriptors() = DESCRIPTORS
	override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
	override fun getDisplayName() = MLPolyRFileType.name
	@Language("MLPolyR")
	override fun getDemoText() = """
let val <VD>n</VD> = { i := 1000 }
    fun <FD>withfresh</FD> <PD>f</PD> = let val <VD>i</VD> = n!i in n!i := i+1; f i end

    (* ---- utilities ---- *)
    fun <FD>Let</FD> (x, e1, e2) = <C>`App</C> (<C>`Lam</C> ([x], e2), [e1])
    fun kv2kb kv = fn v => <C>`App</C> (kv, [v])
    fun kb2kv kb = withfresh (fn rx => <C>`Lam</C> ([rx], kb (<C>`Var</C> rx)))

    fun cvt_app (cvt, e, el, kv) =
    let fun lc (el, kb) =
        case el of [] => kb []
             | e :: el => pc (e, el, fn (v, vl) => kb (v :: vl))
        and pc (e, el, kb) = cvt (e, fn v => lc (el, fn vl => kb (v, vl)))
    in pc (e, el, fn (v, vl) => <C>`App</C> (v, kv :: vl))
    end

    fun cvt_lam (cvt, xl, e) =
    withfresh (fn xk => <C>`Lam</C> (xk :: xl, cvt (e, kv2kb (<C>`Var</C> xk))))

    fun cvt_c (cvt, kb) =
    cases <C>`Const</C> i => kb (<C>`Const</C> i)
            | <C>`Var</C> x => kb (<C>`Var</C> x)
        | <C>`Lam</C> (xl, e) => kb (cvt_lam (cvt, xl, e))
        | <C>`App</C> (e, el) => cvt_app (cvt, e, el, kb2kv kb)
    fun mkConvert (c, e) =
    let fun cvt (e, kb) = match e with c (cvt, kb)
    in cvt_lam (cvt, [], e)
    end
    fun convert e = mkConvert (cvt_c, e)
    fun cvt_if_c other (cvt, kb) =
    cases <C>`If</C> (e1, e2, e3) =>
        withfresh (fn xk =>
        Let (xk, kb2kv kb, cvt (e1, fn v1 =>
            let val kb' = kv2kb (<C>`Var</C> xk)
            in <C>`If</C> (v1, cvt (e2, kb'), cvt (e3, kb'))
        end)))
        default: other (cvt, kb)
    fun cvt_lcc_c other (cvt, kb) =
    cases <C>`LetCC</C> (x, e) => bla
    default: other (cvt, kb)
    fun convert_if e = mkConvert (cvt_if_c cvt_c, e)
in 0
end
"""
}
