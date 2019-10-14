package org.ice1000.tt.editing.narc

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.narc.NarcTokenType
import org.ice1000.tt.psi.narc.NarcTypes
import org.intellij.lang.annotations.Language

object NarcHighlighter : NarcGeneratedHighlighter() {
	@JvmField
	val KEYWORDS = listOf(
		NarcTypes.KW_CLAUSE,
		NarcTypes.KW_DEFINITION,
		NarcTypes.KW_CODATA,
		NarcTypes.KW_DATA,
		NarcTypes.KW_CONSTRUCTOR,
		NarcTypes.KW_PROJECTION,
		NarcTypes.KW_TYPE
	)
	@JvmField
	val OPERATORS = listOf(
		NarcTypes.DOLLAR,
		NarcTypes.ARROW,
		NarcTypes.DOT
	)

	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		NarcTypes.SEMI -> SEMICOLON_KEY
		NarcTypes.IDENTIFIER -> IDENTIFIER_KEY
		NarcTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		NarcTypes.LPAREN, NarcTypes.RPAREN -> PAREN_KEY
		NarcTypes.LBRACE, NarcTypes.RBRACE -> BRACE_KEY
		NarcTypes.LINACCESS, NarcTypes.RINACCESS -> INACCESS_KEY
		in OPERATORS -> OPERATOR_KEY
		in KEYWORDS -> KEYWORD_KEY
		else -> emptyArray()
	}
}

class NarcColorSettingsPage : NarcGeneratedColorSettingsPage() {
	private companion object DescriptorHolder {
		private val DESCRIPTORS = arrayOf(
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), NarcGeneratedHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.identifier"), NarcGeneratedHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.function-decl"), NarcGeneratedHighlighter.FUNCTION_NAME),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.semicolon"), NarcGeneratedHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.unresolved"), NarcGeneratedHighlighter.UNRESOLVED),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.operator"), NarcGeneratedHighlighter.OPERATOR),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.paren"), NarcGeneratedHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.brace"), NarcGeneratedHighlighter.BRACE),
			AttributesDescriptor(TTBundle.message("narc.highlighter.settings.inaccess"), NarcGeneratedHighlighter.INACCESS),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.line-comment"), NarcGeneratedHighlighter.LINE_COMMENT))

		private val ADDITIONAL_DESCRIPTORS = mapOf(
			"FD" to NarcGeneratedHighlighter.FUNCTION_NAME,
			"Unresolved" to NarcGeneratedHighlighter.UNRESOLVED)
	}

	override fun getAdditionalHighlightingTagToDescriptorMap() = ADDITIONAL_DESCRIPTORS
	override fun getAttributeDescriptors() = DESCRIPTORS
	@Language("Narc")
	override fun getDemoText() = """
definition <FD>id</FD> : {A : Type} -> A -> A;
// Identity function definition
clause id a = a;
"""
}
