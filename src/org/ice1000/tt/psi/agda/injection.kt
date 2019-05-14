package org.ice1000.tt.psi.agda

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import org.ice1000.tt.psi.GeneralStringEscaper

abstract class AgdaStringMixin(node: ASTNode) : ASTWrapperPsiElement(node), AgdaString, PsiLanguageInjectionHost {
	override fun isValidHost() = true
	override fun updateText(text: String) = AgdaTokenType.createStr(text, project)?.let(::replace) as? AgdaStringMixin
	override fun createLiteralTextEscaper(): LiteralTextEscaper<AgdaStringMixin> = GeneralStringEscaper(this)
}

class AgdaStringManipulator : AbstractElementManipulator<AgdaStringMixin>() {
	override fun getRangeInElement(element: AgdaStringMixin) = getStringTokenRange(element)
	override fun handleContentChange(psi: AgdaStringMixin, range: TextRange, newContent: String): AgdaStringMixin? {
		val oldText = psi.text
		val newText = oldText.substring(0, range.startOffset) + newContent + oldText.substring(range.endOffset)
		return psi.updateText(newText)
	}

	companion object {
		fun getStringTokenRange(element: AgdaStringMixin) = TextRange.from(1, element.textLength - 2)
	}
}
