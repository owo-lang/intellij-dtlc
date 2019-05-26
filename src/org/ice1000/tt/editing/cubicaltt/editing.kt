package org.ice1000.tt.editing.cubicaltt

import com.intellij.lang.BracePair
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.cubicaltt.CubicalTTTokenType
import org.ice1000.tt.psi.cubicaltt.CubicalTTTypes
import org.ice1000.tt.psi.cubicaltt.cubicalTTLexer

class CubicalTTBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(CubicalTTTypes.LPAREN, CubicalTTTypes.RPAREN, false),
			BracePair(CubicalTTTypes.LT, CubicalTTTypes.GT, false),
			BracePair(CubicalTTTypes.KW_LET, CubicalTTTypes.KW_IN, false),
			BracePair(CubicalTTTypes.LBRACK, CubicalTTTypes.RBRACK, false),
			BracePair(CubicalTTTypes.LAYOUT_START, CubicalTTTypes.LAYOUT_END, false))
	}

	override fun getPairs() = PAIRS
}

class CubicalTTFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(cubicalTTLexer(), CubicalTTTokenType.IDENTIFIERS, CubicalTTTokenType.COMMENTS, TokenSet.EMPTY)
}
