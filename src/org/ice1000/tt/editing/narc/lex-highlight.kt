package org.ice1000.tt.editing.narc

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.psi.narc.NarcTokenType
import org.ice1000.tt.psi.narc.NarcTypes

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
