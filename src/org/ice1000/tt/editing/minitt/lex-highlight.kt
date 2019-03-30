package org.ice1000.tt.editing.minitt

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
import org.ice1000.tt.MiniTTFileType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.MiniTTTokenType
import org.ice1000.tt.psi.MiniTTTypes
import org.ice1000.tt.psi.minitt.minittLexer
import org.intellij.lang.annotations.Language

object MiniTTHighlighter : SyntaxHighlighter {
	@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("MINITT_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
	@JvmField val IDENTIFIER = TextAttributesKey.createTextAttributesKey("MINITT_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
	@JvmField val FUNCTION_NAME = TextAttributesKey.createTextAttributesKey("MINITT_FUNCTION_NAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
	@JvmField val CONSTRUCTOR_CALL = TextAttributesKey.createTextAttributesKey("MINITT_CONSTRUCTOR_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL)
	@JvmField val CONSTRUCTOR_DECL = TextAttributesKey.createTextAttributesKey("MINITT_CONSTRUCTOR_DECL", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
	@JvmField val SEMICOLON = TextAttributesKey.createTextAttributesKey("MINITT_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
	@JvmField val COMMA = TextAttributesKey.createTextAttributesKey("MINITT_COMMA", DefaultLanguageHighlighterColors.COMMA)
	@JvmField val UNRESOLVED = TextAttributesKey.createTextAttributesKey("MINITT_UNRESOLVED", HighlighterColors.BAD_CHARACTER)
	@JvmField val OPERATOR = TextAttributesKey.createTextAttributesKey("MINITT_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
	@JvmField val PAREN = TextAttributesKey.createTextAttributesKey("MINITT_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
	@JvmField val BRACE = TextAttributesKey.createTextAttributesKey("MINITT_BRACES", DefaultLanguageHighlighterColors.BRACES)
	@JvmField val COMMENT = TextAttributesKey.createTextAttributesKey("MINITT_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)

	@JvmField val KEYWORD_KEY = arrayOf(KEYWORD)
	@JvmField val IDENTIFIER_KEY = arrayOf(IDENTIFIER)
	@JvmField val CONSTRUCTOR_KEY = arrayOf(CONSTRUCTOR_CALL)
	@JvmField val SEMICOLON_KEY = arrayOf(SEMICOLON)
	@JvmField val COMMA_KEY = arrayOf(COMMA)
	@JvmField val OPERATOR_KEY = arrayOf(OPERATOR)
	@JvmField val PAREN_KEY = arrayOf(PAREN)
	@JvmField val BRACE_KEY = arrayOf(BRACE)
	@JvmField val COMMENT_KEY = arrayOf(COMMENT)

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

	override fun getHighlightingLexer() = minittLexer()
	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		MiniTTTokenType.LINE_COMMENT -> COMMENT_KEY
		MiniTTTypes.COMMA -> COMMA_KEY
		MiniTTTypes.SEMICOLON -> SEMICOLON_KEY
		MiniTTTypes.IDENTIFIER -> IDENTIFIER_KEY
		MiniTTTypes.CONSTRUCTOR_NAME -> CONSTRUCTOR_KEY
		MiniTTTypes.LEFT_BRACE, MiniTTTypes.RIGHT_BRACE -> BRACE_KEY
		MiniTTTypes.LEFT_PAREN, MiniTTTypes.RIGHT_PAREN -> PAREN_KEY
		in OPERATORS_LIST -> OPERATOR_KEY
		in KEYWORDS_LIST -> KEYWORD_KEY
		else -> emptyArray()
	}
}

class MiniTTHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = MiniTTHighlighter
}

class MiniTTColorSettingsPage : ColorSettingsPage {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.keyword"), MiniTTHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.identifier"), MiniTTHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.constructor-call"), MiniTTHighlighter.CONSTRUCTOR_CALL),
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.constructor-decl"), MiniTTHighlighter.CONSTRUCTOR_DECL),
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.function-decl"), MiniTTHighlighter.FUNCTION_NAME),
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.semicolon"), MiniTTHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.comma"), MiniTTHighlighter.COMMA),
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.unresolved"), MiniTTHighlighter.UNRESOLVED),
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.operator"), MiniTTHighlighter.OPERATOR),
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.paren"), MiniTTHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.brace"), MiniTTHighlighter.BRACE),
			AttributesDescriptor(TTBundle.message("minitt.highlighter.settings.comment"), MiniTTHighlighter.COMMENT))

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
"""
}
