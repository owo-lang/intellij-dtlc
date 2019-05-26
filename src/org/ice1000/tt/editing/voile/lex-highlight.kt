package org.ice1000.tt.editing.voile

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.psi.voile.VoileTokenType
import org.ice1000.tt.psi.voile.VoileTypes

object VoileHighlighter : VoileGeneratedSyntaxHighlighter() {
	@JvmField val OPERATORS = listOf(
		VoileTypes.DOLLAR,
		VoileTypes.ARROW,
		VoileTypes.SUM,
		VoileTypes.DOT,
		VoileTypes.SIG
	)

	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		VoileTypes.CONS -> CONSTRUCTOR_KEY
		VoileTypes.VARIANT -> VARIANT_KEY
		VoileTypes.SEMI -> SEMICOLON_KEY
		VoileTypes.COMMA -> COMMA_KEY
		VoileTypes.IDENTIFIER -> IDENTIFIER_KEY
		VoileTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		VoileTypes.KW_VAL, VoileTypes.KW_LET, VoileTypes.KW_TYPE -> KEYWORD_KEY
		VoileTypes.LPAREN, VoileTypes.RPAREN -> PAREN_KEY
		in OPERATORS -> OPERATOR_KEY
		else -> emptyArray()
	}
}
