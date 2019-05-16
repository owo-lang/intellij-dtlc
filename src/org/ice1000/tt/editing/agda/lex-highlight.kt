package org.ice1000.tt.editing.agda

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
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
import org.ice1000.tt.AgdaFileType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.agda.AgdaTokenType
import org.ice1000.tt.psi.agda.AgdaTypes
import org.ice1000.tt.psi.agda.agdaLexer
import org.intellij.lang.annotations.Language

object AgdaHighlighter : SyntaxHighlighter {
	@JvmField val KEYWORD = TextAttributesKey.createTextAttributesKey("AGDA_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
	@JvmField val IDENTIFIER = TextAttributesKey.createTextAttributesKey("AGDA_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
	@JvmField val FUNCTION_NAME = TextAttributesKey.createTextAttributesKey("AGDA_FUNCTION_NAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
	@JvmField val SEMICOLON = TextAttributesKey.createTextAttributesKey("AGDA_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
	@JvmField val DOT = TextAttributesKey.createTextAttributesKey("AGDA_DOT", DefaultLanguageHighlighterColors.DOT)
	@JvmField val LINE_COMMENT = TextAttributesKey.createTextAttributesKey("AGDA_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
	@JvmField val BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey("AGDA_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
	@JvmField val NUMBER = TextAttributesKey.createTextAttributesKey("AGDA_NUMERAL", DefaultLanguageHighlighterColors.NUMBER)
	@JvmField val STR_LIT = TextAttributesKey.createTextAttributesKey("AGDA_STR_LIT", DefaultLanguageHighlighterColors.STRING)
	@JvmField val CHR_LIT = TextAttributesKey.createTextAttributesKey("AGDA_CHAR_LIT", DefaultLanguageHighlighterColors.STRING)
	@JvmField val FLOAT = TextAttributesKey.createTextAttributesKey("AGDA_FLOAT", DefaultLanguageHighlighterColors.NUMBER)
	@JvmField val ARROW = TextAttributesKey.createTextAttributesKey("AGDA_ARROW", DefaultLanguageHighlighterColors.OPERATION_SIGN)
	@JvmField val HOLE = TextAttributesKey.createTextAttributesKey("AGDA_HOLE", DefaultLanguageHighlighterColors.LABEL)
	@JvmField val PAREN = TextAttributesKey.createTextAttributesKey("AGDA_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
	@JvmField val BRACK = TextAttributesKey.createTextAttributesKey("AGDA_IDIOM", DefaultLanguageHighlighterColors.BRACKETS)
	@JvmField val BRACE = TextAttributesKey.createTextAttributesKey("AGDA_BRACE", DefaultLanguageHighlighterColors.BRACES)
	@JvmField val PRAGMA = TextAttributesKey.createTextAttributesKey("AGDA_PRAGMA", DefaultLanguageHighlighterColors.METADATA)

	@JvmField val KEYWORD_KEY = arrayOf(KEYWORD)
	@JvmField val IDENTIFIER_KEY = arrayOf(IDENTIFIER)
	@JvmField val SEMICOLON_KEY = arrayOf(SEMICOLON)
	@JvmField val DOT_KEY = arrayOf(DOT)
	@JvmField val LINE_COMMENT_KEY = arrayOf(LINE_COMMENT)
	@JvmField val BLOCK_COMMENT_KEY = arrayOf(BLOCK_COMMENT)
	@JvmField val NUMBER_KEY = arrayOf(NUMBER)
	@JvmField val FLOAT_KEY = arrayOf(FLOAT)
	@JvmField val STR_LIT_KEY = arrayOf(STR_LIT)
	@JvmField val CHR_LIT_KEY = arrayOf(CHR_LIT)
	@JvmField val HOLE_KEY = arrayOf(HOLE)
	@JvmField val ARROW_KEY = arrayOf(ARROW)
	@JvmField val PAREN_KEY = arrayOf(PAREN)
	@JvmField val BRACK_KEY = arrayOf(BRACK)
	@JvmField val BRACE_KEY = arrayOf(BRACE)
	@JvmField val PRAGMA_KEY = arrayOf(PRAGMA)

	@JvmField val KEYWORDS = listOf(
		AgdaTypes.KW_NO_ETA_EQUALITY,
		AgdaTypes.KW_ETA_EQUALITY,
		AgdaTypes.KW_QUOTE_CONTEXT,
		AgdaTypes.KW_CONSTRUCTOR,
		AgdaTypes.KW_COINDUCTIVE,
		AgdaTypes.KW_UNQUOTE_DECL,
		AgdaTypes.KW_UNQUOTE_DEF,
		AgdaTypes.KW_POSTULATE,
		AgdaTypes.KW_PRIMITIVE,
		AgdaTypes.KW_INDUCTIVE,
		AgdaTypes.KW_QUOTE_GOAL,
		AgdaTypes.KW_QUOTE_TERM,
		AgdaTypes.KW_VARIABLE,
		AgdaTypes.KW_ABSTRACT,
		AgdaTypes.KW_INSTANCE,
		AgdaTypes.KW_REWRITE,
		AgdaTypes.KW_PRIVATE,
		AgdaTypes.KW_OVERLAP,
		AgdaTypes.KW_UNQUOTE,
		AgdaTypes.KW_PATTERN,
		AgdaTypes.KW_IMPORT,
		AgdaTypes.KW_MODULE,
		AgdaTypes.KW_CODATA,
		AgdaTypes.KW_RECORD,
		AgdaTypes.KW_INFIXL,
		AgdaTypes.KW_INFIXR,
		AgdaTypes.KW_MUTUAL,
		AgdaTypes.KW_FORALL,
		AgdaTypes.KW_TACTIC,
		AgdaTypes.KW_SYNTAX,
		AgdaTypes.KW_WHERE,
		AgdaTypes.KW_FIELD,
		AgdaTypes.KW_INFIX,
		AgdaTypes.KW_MACRO,
		AgdaTypes.KW_QUOTE,
		AgdaTypes.KW_WITH,
		AgdaTypes.KW_OPEN,
		AgdaTypes.KW_DATA,
		AgdaTypes.KW_LET,
		AgdaTypes.KW_IN,
		AgdaTypes.KW_DO
	)

	override fun getHighlightingLexer() = agdaLexer()
	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		AgdaTypes.IDENTIFIER -> IDENTIFIER_KEY
		AgdaTypes.SEMI -> SEMICOLON_KEY
		AgdaTypes.FLOAT -> FLOAT_KEY
		AgdaTypes.NUMBER -> NUMBER_KEY
		AgdaTypes.STR_LIT -> STR_LIT_KEY
		AgdaTypes.CHR_LIT -> CHR_LIT_KEY
		AgdaTypes.HOLE, AgdaTypes.QUESTION_MARK -> HOLE_KEY
		AgdaTypes.ARROW -> ARROW_KEY
		AgdaTypes.OPEN_PAREN, AgdaTypes.CLOSE_PAREN -> PAREN_KEY
		AgdaTypes.OPEN_IDIOM_BRACKET, AgdaTypes.CLOSE_IDIOM_BRACKET -> BRACK_KEY
		AgdaTypes.OPEN_BRACE, AgdaTypes.CLOSE_BRACE -> BRACE_KEY
		AgdaTypes.DOT, AgdaTypes.ELLIPSIS -> DOT_KEY
		AgdaTokenType.PRAGMA -> PRAGMA_KEY
		AgdaTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		AgdaTokenType.BLOCK_COMMENT -> BLOCK_COMMENT_KEY
		in KEYWORDS -> KEYWORD_KEY
		else -> emptyArray()
	}
}

class AgdaHighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = AgdaHighlighter
}

class AgdaColorSettingsPage : ColorSettingsPage {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), AgdaHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.string"), AgdaHighlighter.STR_LIT),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.char"), AgdaHighlighter.CHR_LIT),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.number"), AgdaHighlighter.NUMBER),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.float"), AgdaHighlighter.FLOAT),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.identifier"), AgdaHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.function-decl"), AgdaHighlighter.FUNCTION_NAME),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.semicolon"), AgdaHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.dot"), AgdaHighlighter.DOT),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.hole"), AgdaHighlighter.HOLE),
			AttributesDescriptor(TTBundle.message("agda.highlighter.settings.pragma"), AgdaHighlighter.PRAGMA),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.arrow"), AgdaHighlighter.ARROW),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.paren"), AgdaHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("agda.highlighter.settings.idiom"), AgdaHighlighter.BRACK),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.brace"), AgdaHighlighter.BRACE),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.block-comment"), AgdaHighlighter.BLOCK_COMMENT),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.line-comment"), AgdaHighlighter.LINE_COMMENT))

		private val ADDITIONAL_DESCRIPTORS = mapOf(
			"FD" to AgdaHighlighter.FUNCTION_NAME)
	}

	override fun getHighlighter(): SyntaxHighlighter = AgdaHighlighter
	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	override fun getIcon() = TTIcons.AGDA
	override fun getAttributeDescriptors() = DESCRIPTORS
	override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
	override fun getDisplayName() = AgdaFileType.name
	@Language("Agda")
	override fun getDemoText() = """
{-# OPTIONS --without-K #-}
module Example where
{- Import IO module -}
import IO
import Function

-- Testing function
<FD>test</FD> : (a : IO.IO) -> IO.IO
test a = run $ do
  putStrLn "Hello World"
  (| record
     { label = {! unimplemented !}
     ; c = 'c'
     ; i = 233;
     ; f = -23e666
     } |)
"""
}
