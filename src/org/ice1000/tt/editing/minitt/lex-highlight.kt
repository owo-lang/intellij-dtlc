package org.ice1000.tt.editing.minitt

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.psi.tree.IElementType
import icons.TTIcons
import org.ice1000.tt.MiniTTFileType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.minitt.MiniTTTokenType
import org.ice1000.tt.psi.minitt.MiniTTTypes
import org.intellij.lang.annotations.Language

object MiniTTHighlighter : MiniTTGeneratedSyntaxHighlighter() {
	private val KEYWORDS_LIST = listOf(
		MiniTTTypes.LAMBDA,
		MiniTTTypes.PI,
		MiniTTTypes.TYPE_UNIVERSE,
		MiniTTTypes.SIGMA,
		MiniTTTypes.SUM_KEYWORD,
		MiniTTTypes.SPLIT_KEYWORD,
		MiniTTTypes.CONST_KEYWORD,
		MiniTTTypes.REC_KEYWORD,
		MiniTTTypes.LET_KEYWORD,
		MiniTTTypes.UNIT_KEYWORD,
		MiniTTTypes.ONE_KEYWORD)

	private val OPERATORS_LIST = listOf(
		MiniTTTypes.CONCAT,
		MiniTTTypes.MUL,
		MiniTTTypes.ARROW,
		MiniTTTypes.DOUBLE_ARROW,
		MiniTTTypes.DOT_TWO,
		MiniTTTypes.DOT_ONE)

	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		MiniTTTokenType.LINE_COMMENT -> COMMENT_KEY
		MiniTTTypes.COMMA -> COMMA_KEY
		MiniTTTypes.SEMICOLON -> SEMICOLON_KEY
		MiniTTTypes.IDENTIFIER -> IDENTIFIER_KEY
		MiniTTTypes.CONSTRUCTOR_NAME -> CONSTRUCTOR_CALL_KEY
		MiniTTTypes.LEFT_BRACE, MiniTTTypes.RIGHT_BRACE -> BRACE_KEY
		MiniTTTypes.LEFT_PAREN, MiniTTTypes.RIGHT_PAREN -> PAREN_KEY
		in OPERATORS_LIST -> OPERATOR_KEY
		in KEYWORDS_LIST -> KEYWORD_KEY
		else -> emptyArray()
	}
}

class MiniTTColorSettingsPage : ColorSettingsPage {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), MiniTTHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.identifier"), MiniTTHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.constructor-call"), MiniTTHighlighter.CONSTRUCTOR_CALL),
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.constructor-decl"), MiniTTHighlighter.CONSTRUCTOR_DECL),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.function-decl"), MiniTTHighlighter.FUNCTION_NAME),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.semicolon"), MiniTTHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comma"), MiniTTHighlighter.COMMA),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.unresolved"), MiniTTHighlighter.UNRESOLVED),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.operator"), MiniTTHighlighter.OPERATOR),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.paren"), MiniTTHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.brace"), MiniTTHighlighter.BRACE),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comment"), MiniTTHighlighter.COMMENT))

		private val ADDITIONAL_DESCRIPTORS = mapOf(
			"Unresolved" to MiniTTHighlighter.UNRESOLVED,
			"CCl" to MiniTTHighlighter.CONSTRUCTOR_CALL,
			"FDl" to MiniTTHighlighter.FUNCTION_NAME,
			"CDl" to MiniTTHighlighter.CONSTRUCTOR_DECL)
	}

	override fun getHighlighter(): SyntaxHighlighter = MiniTTHighlighter
	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	override fun getIcon() = TTIcons.MINI_TT
	override fun getAttributeDescriptors() = DESCRIPTORS
	override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
	override fun getDisplayName() = MiniTTFileType.name
	@Language("Mini-TT")
	override fun getDemoText() = """
let _: Type = Sum { <CDl>True</CDl> | <CDl>False</CDl> } ++ Sum { <CDl>TT</CDl> };

rec <FDl>nat</FDl> : Type = Sum { <CDl>Zero</CDl> | <CDl>Suc</CDl> nat };
let <FDl>one</FDl>: nat = <CCl>Suc</CCl> <CCl>Zero</CCl>;
-- Comments
let <FDl>maybe</FDl>: Type -> Type = \lambda t. Sum { <CDl>Just</CDl> t | <CDl>Nothing</CDl> };
let <FDl>unwrap_type</FDl> (t : Type): (maybe t) -> Type = split
  { <CDl>Just</CDl> _ => t | <CDl>Nothing</CDl> => 1 };

const bad = <Unresolved>bbba</Unresolved>;
"""
}
