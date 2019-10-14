package org.ice1000.tt.editing.acore

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.acore.ACoreTokenType
import org.ice1000.tt.psi.acore.ACoreTypes
import org.intellij.lang.annotations.Language

object ACoreHighlighter : ACoreGeneratedHighlighter() {
	private val KEYWORDS_LIST = listOf(
		ACoreTypes.LAMBDA,
		ACoreTypes.PI,
		ACoreTypes.TYPE_UNIVERSE,
		ACoreTypes.SIGMA,
		ACoreTypes.SUM_TOKEN,
		ACoreTypes.FUN_TOKEN,
		ACoreTypes.LETREC_TOKEN,
		ACoreTypes.LET_TOKEN,
		ACoreTypes.UNIT_TOKEN,
		ACoreTypes.VOID,
		ACoreTypes.ONE_TOKEN)

	private val OPERATORS_LIST = listOf(
		ACoreTypes.MUL,
		ACoreTypes.ARROW,
		ACoreTypes.DOLLAR,
		ACoreTypes.DOT_TWO,
		ACoreTypes.DOT_ONE)

	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		ACoreTypes.COMMA -> COMMA_KEY
		ACoreTypes.SEMICOLON -> SEMICOLON_KEY
		ACoreTypes.IDENTIFIER -> IDENTIFIER_KEY
		ACoreTypes.LEFT_PAREN, ACoreTypes.RIGHT_PAREN -> PAREN_KEY
		in OPERATORS_LIST -> OPERATOR_KEY
		in KEYWORDS_LIST -> KEYWORD_KEY
		ACoreTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		ACoreTokenType.BLOCK_COMMENT -> BLOCK_COMMENT_KEY
		else -> emptyArray()
	}
}

class ACoreColorSettingsPage : ACoreGeneratedColorSettingsPage() {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), ACoreGeneratedHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.identifier"), ACoreGeneratedHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.function-decl"), ACoreGeneratedHighlighter.FUNCTION_NAME),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.semicolon"), ACoreGeneratedHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comma"), ACoreGeneratedHighlighter.COMMA),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.unresolved"), ACoreGeneratedHighlighter.UNRESOLVED),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.operator"), ACoreGeneratedHighlighter.OPERATOR),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.paren"), ACoreGeneratedHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.line-comment"), ACoreGeneratedHighlighter.LINE_COMMENT),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.block-comment"), ACoreGeneratedHighlighter.BLOCK_COMMENT))

		private val ADDITIONAL_DESCRIPTORS = mapOf(
			"Unresolved" to ACoreGeneratedHighlighter.UNRESOLVED,
			"FDl" to ACoreGeneratedHighlighter.FUNCTION_NAME)
	}

	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	override fun getAttributeDescriptors() = DESCRIPTORS
	@Language("Vanilla Mini-TT")
	override fun getDemoText() = """
let <FDl>Bool</FDl>: U = Sum ( true | false );
letrec <FDl>Nat</FDl>: U = Sum ( zero | succ Nat );

let bad: U = <Unresolved>unresolved</Unresolved>;
Void
"""
}
