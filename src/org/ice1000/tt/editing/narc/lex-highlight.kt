package org.ice1000.tt.editing.narc

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.narc.NarcTokenType
import org.ice1000.tt.psi.narc.NarcTypes
import org.intellij.lang.annotations.Language

object NarcHighlighter : NarcGeneratedSyntaxHighlighter() {
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
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.keyword"), NarcHighlighter.KEYWORD),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.identifier"), NarcHighlighter.IDENTIFIER),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.function-decl"), NarcHighlighter.FUNCTION_NAME),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.semicolon"), NarcHighlighter.SEMICOLON),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.unresolved"), NarcHighlighter.UNRESOLVED),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.operator"), NarcHighlighter.OPERATOR),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.paren"), NarcHighlighter.PAREN),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.brace"), NarcHighlighter.BRACE),
			AttributesDescriptor(TTBundle.message("narc.highlighter.settings.inaccess"), NarcHighlighter.INACCESS),
			AttributesDescriptor(TTBundle.message("tt.highlighter.settings.line-comment"), NarcHighlighter.LINE_COMMENT))

		private val ADDITIONAL_DESCRIPTORS = mapOf(
			"FD" to NarcHighlighter.FUNCTION_NAME,
			"Unresolved" to NarcHighlighter.UNRESOLVED)
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
