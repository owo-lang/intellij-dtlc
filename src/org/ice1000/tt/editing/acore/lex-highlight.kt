package org.ice1000.tt.editing.acore

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
import org.ice1000.tt.ACoreFileType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.acore.ACoreTokenType
import org.ice1000.tt.psi.acore.ACoreTypes
import org.ice1000.tt.psi.acore.acoreLexer
import org.intellij.lang.annotations.Language

object ACoreHighlighter : SyntaxHighlighter {
	@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("AGDA_CORE_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
	@JvmField val IDENTIFIER = TextAttributesKey.createTextAttributesKey("AGDA_CORE_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
	@JvmField val FUNCTION_NAME = TextAttributesKey.createTextAttributesKey("AGDA_CORE_FUNCTION_NAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
	@JvmField val SEMICOLON = TextAttributesKey.createTextAttributesKey("AGDA_CORE_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
	@JvmField val COMMA = TextAttributesKey.createTextAttributesKey("AGDA_CORE_COMMA", DefaultLanguageHighlighterColors.COMMA)
	@JvmField val UNRESOLVED = TextAttributesKey.createTextAttributesKey("AGDA_CORE_UNRESOLVED", HighlighterColors.BAD_CHARACTER)
	@JvmField val OPERATOR = TextAttributesKey.createTextAttributesKey("AGDA_CORE_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
	@JvmField val PAREN = TextAttributesKey.createTextAttributesKey("AGDA_CORE_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
	@JvmField val COMMENT = TextAttributesKey.createTextAttributesKey("AGDA_CORE_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)

	@JvmField val KEYWORD_KEY = arrayOf(KEYWORD)
	@JvmField val IDENTIFIER_KEY = arrayOf(IDENTIFIER)
	@JvmField val SEMICOLON_KEY = arrayOf(SEMICOLON)
	@JvmField val COMMA_KEY = arrayOf(COMMA)
	@JvmField val OPERATOR_KEY = arrayOf(OPERATOR)
	@JvmField val PAREN_KEY = arrayOf(PAREN)
	@JvmField val COMMENT_KEY = arrayOf(COMMENT)

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

	override fun getHighlightingLexer() = acoreLexer()
	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		ACoreTypes.COMMA -> COMMA_KEY
		ACoreTypes.SEMICOLON -> SEMICOLON_KEY
		ACoreTypes.IDENTIFIER -> IDENTIFIER_KEY
		ACoreTypes.LEFT_PAREN, ACoreTypes.RIGHT_PAREN -> PAREN_KEY
		in OPERATORS_LIST -> OPERATOR_KEY
		in KEYWORDS_LIST -> KEYWORD_KEY
		in ACoreTokenType.COMMENTS -> COMMENT_KEY
		else -> emptyArray()
	}
}

class ACoreHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = ACoreHighlighter
}

class ACoreColorSettingsPage : ColorSettingsPage {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), ACoreHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.identifier"), ACoreHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("acore.highlighter.settings.function-decl"), ACoreHighlighter.FUNCTION_NAME),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.semicolon"), ACoreHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comma"), ACoreHighlighter.COMMA),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.unresolved"), ACoreHighlighter.UNRESOLVED),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.operator"), ACoreHighlighter.OPERATOR),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.paren"), ACoreHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.comment"), ACoreHighlighter.COMMENT))

		private val ADDITIONAL_DESCRIPTORS = mapOf(
			"Unresolved" to ACoreHighlighter.UNRESOLVED,
			"FDl" to ACoreHighlighter.FUNCTION_NAME)
	}

	override fun getHighlighter(): SyntaxHighlighter = ACoreHighlighter
	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	override fun getIcon() = TTIcons.AGDA_CORE
	override fun getAttributeDescriptors() = DESCRIPTORS
	override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
	override fun getDisplayName() = ACoreFileType.name
	@Language("Vanilla Mini-TT")
	override fun getDemoText() = """
let <FDl>Bool</FDl>: U = Sum ( true | false );
letrec <FDl>Nat</FDl>: U = Sum ( zero | succ Nat );

let bad: U = <Unresolved>unresolved</Unresolved>;
Void
"""
}
