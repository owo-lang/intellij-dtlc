package org.ice1000.tt.editing.mlang

import com.intellij.lang.BracePair
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.psi.mlang.MlangTypes

class MlangBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(MlangTypes.LBRACE, MlangTypes.RBRACE, false),
			BracePair(MlangTypes.LBRACK, MlangTypes.RBRACK, false),
			BracePair(MlangTypes.LPAREN, MlangTypes.RPAREN, false))
	}

	override fun getPairs() = PAIRS
}
