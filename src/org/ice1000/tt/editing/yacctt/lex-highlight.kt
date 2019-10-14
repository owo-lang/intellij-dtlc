package org.ice1000.tt.editing.yacctt

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.yacctt.YaccTTTokenType
import org.ice1000.tt.psi.yacctt.YaccTTTypes
import org.intellij.lang.annotations.Language

object YaccTTHighlighter : YaccTTGeneratedHighlighter() {
	private val KEYWORDS = listOf(
		YaccTTTypes.KW_TRANSPARENT_ALL,
		YaccTTTypes.KW_TRANSPARENT,
		YaccTTTypes.KW_TRANSPORT,
		YaccTTTypes.KW_IMPORT,
		YaccTTTypes.KW_MODULE,
		YaccTTTypes.KW_MUTUAL,
		YaccTTTypes.KW_OPAQUE,
		YaccTTTypes.KW_SPLIT_AT,
		YaccTTTypes.KW_HDATA,
		YaccTTTypes.KW_LINE_P,
		YaccTTTypes.KW_PATH_P,
		YaccTTTypes.KW_SPLIT,
		YaccTTTypes.KW_V_PROJ,
		YaccTTTypes.KW_WHERE,
		YaccTTTypes.KW_DATA,
		YaccTTTypes.KW_HCOM,
		YaccTTTypes.KW_WITH,
		YaccTTTypes.KW_LET,
		YaccTTTypes.KW_BOX,
		YaccTTTypes.KW_CAP,
		YaccTTTypes.KW_COM,
		YaccTTTypes.KW_V_IN,
		YaccTTTypes.KW_IN,
		YaccTTTypes.KW_V,
		YaccTTTypes.KW_U
	)

	override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> = when (tokenType) {
		YaccTTTypes.IDENTIFIER -> IDENTIFIER_KEY
		YaccTTTypes.LAYOUT_SEP -> SEMICOLON_KEY
		YaccTTTypes.LPAREN, YaccTTTypes.RPAREN -> PAREN_KEY
		YaccTTTypes.LBRACK, YaccTTTypes.RBRACK -> BRACK_KEY
		YaccTTTypes.COMMA -> COMMA_KEY
		YaccTTTypes.KW_UNDEFINED -> UNDEFINED_KEY
		YaccTTTypes.HOLE -> HOLE_KEY
		YaccTTTypes.ZERO, YaccTTTypes.ONE -> DIMENSION_KEY
		YaccTTTypes.DOT_ONE, YaccTTTypes.DOT_TWO -> PROJECTION_KEY
		YaccTTTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		YaccTTTokenType.BLOCK_COMMENT -> BLOCK_COMMENT_KEY
		in KEYWORDS -> KEYWORD_KEY
		else -> emptyArray()
	}
}

class YaccTTColorSettingsPage : YaccTTGeneratedColorSettingsPage() {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), YaccTTGeneratedHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.identifier"), YaccTTGeneratedHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.function-decl"), YaccTTGeneratedHighlighter.FUNCTION_NAME),
			AttributesDescriptor(TTBundle.message("cubicaltt.highlighter.settings.datatype-decl"), YaccTTGeneratedHighlighter.DATATYPE_NAME),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.semicolon"), YaccTTGeneratedHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comma"), YaccTTGeneratedHighlighter.COMMA),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.paren"), YaccTTGeneratedHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.bracket"), YaccTTGeneratedHighlighter.BRACK),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.hole"), YaccTTGeneratedHighlighter.HOLE),
			AttributesDescriptor(TTBundle.message("cubicaltt.highlighter.settings.undefined"), YaccTTGeneratedHighlighter.UNDEFINED),
			AttributesDescriptor(TTBundle.message("cubicaltt.highlighter.settings.dim"), YaccTTGeneratedHighlighter.DIMENSION),
			AttributesDescriptor(TTBundle.message("cubicaltt.highlighter.settings.projection"), YaccTTGeneratedHighlighter.PROJECTION),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.line-comment"), YaccTTGeneratedHighlighter.LINE_COMMENT),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.block-comment"), YaccTTGeneratedHighlighter.BLOCK_COMMENT))

		private val ADDITIONAL_DESCRIPTORS = mapOf(
			"DD" to YaccTTGeneratedHighlighter.DATATYPE_NAME,
			"FD" to YaccTTGeneratedHighlighter.FUNCTION_NAME)
	}

	override fun getAttributeDescriptors() = DESCRIPTORS
	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	@Language("YaccTT")
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
