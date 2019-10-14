package org.ice1000.tt.editing.agda

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.agda.AgdaTokenType
import org.ice1000.tt.psi.agda.AgdaTypes
import org.intellij.lang.annotations.Language

object AgdaHighlighter : AgdaGeneratedSyntaxHighlighter() {
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
		AgdaTypes.KW_DO,
		AgdaTypes.KW_HIDING,
		AgdaTypes.KW_RENAMING,
		AgdaTypes.KW_USING
	)

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

class AgdaColorSettingsPage : AgdaGeneratedColorSettingsPage() {
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

	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	override fun getAttributeDescriptors() = DESCRIPTORS
	@Language("Agda")
	override fun getDemoText() = """
{-# OPTIONS --without-K #-}
module Example where
{- Import IO module -}
import IO using (refl; _==_) renaming (sym to asym)
import Function

-- Testing function
<FD>test</FD> : (a : IO.IO) -> IO.IO
test a = run $ do
  putStrLn "Hello World"
  (| record
     { label = {! unimplemented !}
     ; c = 'c'
     ; i = 233
     ; f = -23e666
     } |)
"""
}
