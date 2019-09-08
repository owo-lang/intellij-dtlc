package org.ice1000.tt.editing.mlang

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.psi.mlang.MlangTypes

object MlangHighlighter : MlangGeneratedSyntaxHighlighter() {
	@JvmField val KEYWORDS = listOf(
		MlangTypes.KW_DECLARE,
		MlangTypes.KW_DEFINE,
		MlangTypes.KW_PARAMETERS,
		MlangTypes.KW_INDUCTIVELY,
		MlangTypes.KW_WITH_CONSTRUCTOR,
		MlangTypes.KW_DEBUG
	)

	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		in KEYWORDS -> KEYWORD_KEY
		MlangTypes.IDENTIFIER -> IDENTIFIER_KEY
		else -> emptyArray()
	}
}
