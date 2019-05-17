package org.ice1000.tt.editing.agda

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.psi.agda.AgdaTypes

class AgdaBraceMatcher : PairedBraceMatcher {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(AgdaTypes.OPEN_BRACE, AgdaTypes.CLOSE_BRACE, false),
			BracePair(AgdaTypes.OPEN_IDIOM_BRACKET, AgdaTypes.CLOSE_IDIOM_BRACKET, false),
			BracePair(AgdaTypes.KW_LET, AgdaTypes.KW_IN, false),
			BracePair(AgdaTypes.OPEN_PAREN, AgdaTypes.CLOSE_PAREN, false))
	}

	override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset
	override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true
	override fun getPairs() = PAIRS
}
