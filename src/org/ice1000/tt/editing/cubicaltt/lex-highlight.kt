package org.ice1000.tt.editing.cubicaltt

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.cubicaltt.CubicalTTTokenType
import org.ice1000.tt.psi.cubicaltt.CubicalTTTypes
import org.intellij.lang.annotations.Language

object CubicalTTHighlighter : CubicalTTGeneratedHighlighter() {
	private val KEYWORDS = listOf(
		CubicalTTTypes.KW_TRANSPARENT_ALL,
		CubicalTTTypes.KW_TRANSPARENT,
		CubicalTTTypes.KW_TRANSPORT,
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

	override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> = when (tokenType) {
		CubicalTTTypes.IDENTIFIER -> IDENTIFIER_KEY
		CubicalTTTypes.LAYOUT_SEP -> SEMICOLON_KEY
		CubicalTTTypes.LPAREN, CubicalTTTypes.RPAREN -> PAREN_KEY
		CubicalTTTypes.LBRACK, CubicalTTTypes.RBRACK -> BRACK_KEY
		CubicalTTTypes.COMMA -> COMMA_KEY
		CubicalTTTypes.KW_UNDEFINED -> UNDEFINED_KEY
		CubicalTTTypes.HOLE -> HOLE_KEY
		CubicalTTTypes.ZERO, CubicalTTTypes.ONE -> DIMENSION_KEY
		CubicalTTTypes.DOT_ONE, CubicalTTTypes.DOT_TWO -> PROJECTION_KEY
		CubicalTTTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		CubicalTTTokenType.BLOCK_COMMENT -> BLOCK_COMMENT_KEY
		in KEYWORDS -> KEYWORD_KEY
		else -> emptyArray()
	}
}

class CubicalTTColorSettingsPage : CubicalTTGeneratedColorSettingsPage() {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), CubicalTTGeneratedHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.identifier"), CubicalTTGeneratedHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.function-decl"), CubicalTTGeneratedHighlighter.FUNCTION_NAME),
			AttributesDescriptor(TTBundle.message("cubicaltt.highlighter.settings.datatype-decl"), CubicalTTGeneratedHighlighter.DATATYPE_NAME),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.semicolon"), CubicalTTGeneratedHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comma"), CubicalTTGeneratedHighlighter.COMMA),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.paren"), CubicalTTGeneratedHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.bracket"), CubicalTTGeneratedHighlighter.BRACK),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.hole"), CubicalTTGeneratedHighlighter.HOLE),
			AttributesDescriptor(TTBundle.message("cubicaltt.highlighter.settings.undefined"), CubicalTTGeneratedHighlighter.UNDEFINED),
			AttributesDescriptor(TTBundle.message("cubicaltt.highlighter.settings.dim"), CubicalTTGeneratedHighlighter.DIMENSION),
			AttributesDescriptor(TTBundle.message("cubicaltt.highlighter.settings.projection"), CubicalTTGeneratedHighlighter.PROJECTION),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.line-comment"), CubicalTTGeneratedHighlighter.LINE_COMMENT),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.block-comment"), CubicalTTGeneratedHighlighter.BLOCK_COMMENT))

		private val ADDITIONAL_DESCRIPTORS = mapOf(
			"DD" to CubicalTTGeneratedHighlighter.DATATYPE_NAME,
			"FD" to CubicalTTGeneratedHighlighter.FUNCTION_NAME)
	}

	override fun getAttributeDescriptors() = DESCRIPTORS
	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	@Language("CubicalTT")
	override fun getDemoText() = """
module test where

<FD>postulate</FD> : U*U = undefined
<FD>wont_work</FD> : U = postulate .1

data <DD>pTrunc</DD> (A : U)
  = inc (a : A)
  | inh (x y : pTrunc A) <i> [(i=0) -> ?, (i=1) -> y]

<FD>pTruncIsProp</FD> (A : U) : prop (pTrunc A) =
 \ (x y : pTrunc A) -> <i> inh{pTrunc A} x y @ i
"""
}
