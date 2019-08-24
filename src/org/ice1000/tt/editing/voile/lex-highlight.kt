package org.ice1000.tt.editing.voile

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.voile.VoileTokenType
import org.ice1000.tt.psi.voile.VoileTypes
import org.intellij.lang.annotations.Language

object VoileHighlighter : VoileGeneratedSyntaxHighlighter() {
	@JvmField val KEYWORDS = listOf(
		VoileTypes.KW_VAL,
		VoileTypes.KW_LET,
		VoileTypes.KW_CASE,
		VoileTypes.KW_OR,
		VoileTypes.KW_NOCASES,
		VoileTypes.KW_SUM,
		VoileTypes.KW_REC,
		VoileTypes.KW_TYPE
	)
	@JvmField val OPERATORS = listOf(
		VoileTypes.DOLLAR,
		VoileTypes.ARROW,
		VoileTypes.DOT,
		VoileTypes.SIG
	)

	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		VoileTypes.CONS -> CONSTRUCTOR_KEY
		VoileTypes.VARIANT -> VARIANT_KEY
		VoileTypes.SEMI -> SEMICOLON_KEY
		VoileTypes.COMMA -> COMMA_KEY
		VoileTypes.IDENTIFIER -> IDENTIFIER_KEY
		VoileTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		VoileTypes.LPAREN, VoileTypes.RPAREN -> PAREN_KEY
		VoileTypes.LBRACE, VoileTypes.RBRACE -> BRACE_KEY
		VoileTypes.LBRACE2, VoileTypes.RBRACE2 -> BRACE2_KEY
		in OPERATORS -> OPERATOR_KEY
		in KEYWORDS -> KEYWORD_KEY
		else -> emptyArray()
	}
}
class VoileColorSettingsPage : VoileGeneratedColorSettingsPage() {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), VoileHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.identifier"), VoileHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.function-decl"), VoileHighlighter.FUNCTION_NAME),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.semicolon"), VoileHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comma"), VoileHighlighter.COMMA),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.unresolved"), VoileHighlighter.UNRESOLVED),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.operator"), VoileHighlighter.OPERATOR),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.paren"), VoileHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.brace"), VoileHighlighter.BRACE),
			AttributesDescriptor(TTBundle.message("voile.highlighter.settings.brace"), VoileHighlighter.BRACE2),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.line-comment"), VoileHighlighter.LINE_COMMENT))

		private val ADDITIONAL_DESCRIPTORS = mapOf(
			"FD" to VoileHighlighter.FUNCTION_NAME,
			"Unresolved" to VoileHighlighter.UNRESOLVED)
	}

	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	override fun getAttributeDescriptors() = DESCRIPTORS
	@Language("Voile")
	override fun getDemoText() = """
val <FD>id</FD>: (A: Type) -> A -> Rec { lab: A; };
// Identity function definition
let <FD>id</FD> = \A a. {| lab = a; |};
"""
}
