package org.ice1000.tt.editing.yacctt

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.psi.tree.IElementType
import icons.TTIcons
import org.ice1000.tt.YaccTTFileType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.yacctt.YaccTTTokenType
import org.ice1000.tt.psi.yacctt.YaccTTTypes
import org.intellij.lang.annotations.Language

object YaccTTHighlighter : YaccTTGeneratedSyntaxHighlighter() {
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
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), YaccTTHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.identifier"), YaccTTHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.function-decl"), YaccTTHighlighter.FUNCTION_NAME),
			AttributesDescriptor(TTBundle.message("cubicaltt.highlighter.settings.datatype-decl"), YaccTTHighlighter.DATATYPE_NAME),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.semicolon"), YaccTTHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comma"), YaccTTHighlighter.COMMA),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.paren"), YaccTTHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.bracket"), YaccTTHighlighter.BRACK),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.hole"), YaccTTHighlighter.HOLE),
			AttributesDescriptor(TTBundle.message("cubicaltt.highlighter.settings.undefined"), YaccTTHighlighter.UNDEFINED),
			AttributesDescriptor(TTBundle.message("cubicaltt.highlighter.settings.dim"), YaccTTHighlighter.DIMENSION),
			AttributesDescriptor(TTBundle.message("cubicaltt.highlighter.settings.projection"), YaccTTHighlighter.PROJECTION),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.line-comment"), YaccTTHighlighter.LINE_COMMENT),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.block-comment"), YaccTTHighlighter.BLOCK_COMMENT))

		private val ADDITIONAL_DESCRIPTORS = mapOf(
			"DD" to YaccTTHighlighter.DATATYPE_NAME,
			"FD" to YaccTTHighlighter.FUNCTION_NAME)
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
