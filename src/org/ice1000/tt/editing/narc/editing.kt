package org.ice1000.tt.editing.narc

import com.intellij.lang.BracePair
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.psi.narc.NarcTypes

class NarcBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(NarcTypes.LBRACE, NarcTypes.RBRACE, false),
			BracePair(NarcTypes.LINACCESS, NarcTypes.RINACCESS, false),
			BracePair(NarcTypes.LPAREN, NarcTypes.RPAREN, false))
	}

	override fun getPairs() = PAIRS
}
