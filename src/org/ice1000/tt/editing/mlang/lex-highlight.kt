package org.ice1000.tt.editing.mlang

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.psi.mlang.MlangTokenType
import org.ice1000.tt.psi.mlang.MlangTypes

object MlangHighlighter : MlangGeneratedSyntaxHighlighter() {
	@JvmField val KEYWORDS = listOf(
		MlangTypes.KW_DECLARE,
		MlangTypes.KW_DEFINE,
		MlangTypes.KW_TYPE,
		MlangTypes.KW_RUN,
		MlangTypes.KW_MAKE,
		MlangTypes.KW_PARAMETERS,
		MlangTypes.KW_INTERVAL,
		MlangTypes.KW_UNDEFINED,
		MlangTypes.KW_FIELD,
		MlangTypes.KW_SUM,
		MlangTypes.KW_CASE,
		MlangTypes.KW_AS,
		MlangTypes.KW_RECORD,
		MlangTypes.KW_TRANSP,
		MlangTypes.KW_UNGLUE,
		MlangTypes.KW_INDUCTIVELY,
		MlangTypes.KW_WITH_CONSTRUCTOR,
		MlangTypes.KW_DEBUG
	)

	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		in KEYWORDS -> KEYWORD_KEY
		MlangTypes.IDENTIFIER -> IDENTIFIER_KEY
		MlangTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		MlangTokenType.BLOCK_COMMENT -> BLOCK_COMMENT_KEY
		MlangTypes.LPAREN, MlangTypes.RPAREN -> PAREN_KEY
		MlangTypes.LPAREN, MlangTypes.RBRACE -> BRACE_KEY
		else -> emptyArray()
	}
}
