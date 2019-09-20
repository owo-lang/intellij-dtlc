package org.ice1000.tt.editing.mlang

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.mlang.MlangTokenType
import org.ice1000.tt.psi.mlang.MlangTypes
import org.intellij.lang.annotations.Language

object MlangHighlighter : MlangGeneratedSyntaxHighlighter() {
	@JvmField val KEYWORDS = listOf(
		MlangTypes.KW_DECLARE,
		MlangTypes.KW_DEFINE,
		MlangTypes.KW_TYPE,
		MlangTypes.KW_RUN,
		MlangTypes.KW_MAKE,
		MlangTypes.KW_PARAMETERS,
		MlangTypes.KW_INTERVAL,
		MlangTypes.KW_UNDEFINED,
		MlangTypes.KW_FIELD,
		MlangTypes.KW_SUM,
		MlangTypes.KW_CASE,
		MlangTypes.KW_AS,
		MlangTypes.KW_RECORD,
		MlangTypes.KW_TRANSP,
		MlangTypes.KW_HCOMP,
		MlangTypes.KW_HFILL,
		MlangTypes.KW_GLUE,
		MlangTypes.KW_GLUE_TYPE,
		MlangTypes.KW_UNGLUE,
		MlangTypes.KW_INDUCTIVELY,
		MlangTypes.KW_WITH_CONSTRUCTOR,
		MlangTypes.KW_DEBUG
	)

	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		in KEYWORDS -> KEYWORD_KEY
		MlangTypes.IDENTIFIER -> IDENTIFIER_KEY
		MlangTypes.DIM -> DIMENSION_KEY
		MlangTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		MlangTokenType.BLOCK_COMMENT -> BLOCK_COMMENT_KEY
		MlangTypes.LPAREN, MlangTypes.RPAREN -> PAREN_KEY
		MlangTypes.LPAREN, MlangTypes.RBRACE -> BRACE_KEY
		else -> emptyArray()
	}
}

class MlangColorSettingsPage : MlangGeneratedColorSettingsPage() {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), MlangHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.identifier"), MlangHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.function-decl"), MlangHighlighter.FUNCTION_NAME),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comma"), MlangHighlighter.COMMA),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.paren"), MlangHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.brace"), MlangHighlighter.BRACE),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.bracket"), MlangHighlighter.BRACK),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.line-comment"), MlangHighlighter.LINE_COMMENT))

		private val ADDITIONAL_DESCRIPTORS = mapOf(
			"FD" to MlangHighlighter.FUNCTION_NAME)
	}

	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	override fun getAttributeDescriptors() = DESCRIPTORS
	@Language("Mlang")
	override fun getDemoText() = """
// Comments
/* Block comments */
define <FD>myLovelyFunction</FD>: (A: type, a b : A, p : a ≡[A] b) = run {
  expr
}
"""
}
