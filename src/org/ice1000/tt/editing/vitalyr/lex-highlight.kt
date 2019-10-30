package org.ice1000.tt.editing.vitalyr

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.psi.vitalyr.VitalyRTokenType
import org.ice1000.tt.psi.vitalyr.VitalyRTypes

object VitalyRHighlighter : VitalyRGeneratedHighlighter() {
	override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> = when (type) {
		VitalyRTypes.IDENTIFIER -> IDENTIFIER_KEY
		VitalyRTypes.KW_LAMBDA -> KEYWORD_KEY
		VitalyRTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		VitalyRTypes.LPAREN, VitalyRTypes.RPAREN -> PAREN_KEY
		else -> emptyArray()
	}
}
