package org.ice1000.tt.editing.redprl

import com.intellij.lang.BracePair
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.redprl.RedPrlElementType.redprlLexer
import org.ice1000.tt.psi.redprl.RedPrlTokenType
import org.ice1000.tt.psi.redprl.RedPrlTypes

class RedPrlBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(RedPrlTypes.LPAREN, RedPrlTypes.RPAREN, false),
			BracePair(RedPrlTypes.LSQUARE, RedPrlTypes.RSQUARE, false),
			BracePair(RedPrlTypes.LBRACKET, RedPrlTypes.RBRACKET, false))
	}

	override fun getPairs() = PAIRS
}

class RedPrlFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(redprlLexer(), RedPrlTokenType.IDENTIFIERS, RedPrlTokenType.COMMENTS, TokenSet.EMPTY)
}
