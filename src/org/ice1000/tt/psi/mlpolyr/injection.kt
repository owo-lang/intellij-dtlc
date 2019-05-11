package org.ice1000.tt.psi.mlpolyr

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import org.ice1000.tt.psi.GeneralStringEscaper
import org.ice1000.tt.psi.mlpolyr.impl.MLPolyRExpImpl

abstract class MLPolyRStringMixin(node: ASTNode) : MLPolyRExpImpl(node), MLPolyRString, PsiLanguageInjectionHost {
	override fun isValidHost() = true
	override fun updateText(text: String) = MLPolyRTokenType.createStr(text, project)?.let(::replace) as? MLPolyRStringMixin
	override fun createLiteralTextEscaper(): LiteralTextEscaper<MLPolyRStringMixin> = GeneralStringEscaper(this)
}

class MLPolyRStringManipulator : AbstractElementManipulator<MLPolyRStringMixin>() {
	override fun getRangeInElement(element: MLPolyRStringMixin) = getStringTokenRange(element)
	override fun handleContentChange(psi: MLPolyRStringMixin, range: TextRange, newContent: String): MLPolyRStringMixin? {
		val oldText = psi.text
		val newText = oldText.substring(0, range.startOffset) + newContent + oldText.substring(range.endOffset)
		return psi.updateText(newText)
	}

	companion object {
		fun getStringTokenRange(element: MLPolyRStringMixin) = TextRange.from(1, element.textLength - 2)
	}
}
