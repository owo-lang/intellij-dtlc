package org.ice1000.tt.editing.agda

import com.intellij.lang.BracePair
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.psi.agda.AgdaTypes

class AgdaBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(AgdaTypes.OPEN_BRACE, AgdaTypes.CLOSE_BRACE, false),
			BracePair(AgdaTypes.OPEN_IDIOM_BRACKET, AgdaTypes.CLOSE_IDIOM_BRACKET, false),
			BracePair(AgdaTypes.KW_LET, AgdaTypes.KW_IN, false),
			BracePair(AgdaTypes.OPEN_PAREN, AgdaTypes.CLOSE_PAREN, false))
	}

	override fun getPairs() = PAIRS
}
