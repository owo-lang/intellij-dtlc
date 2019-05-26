package org.ice1000.tt.editing.minitt

import com.intellij.lang.BracePair
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.editing.TTCommenter
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.minitt.MiniTTTokenType
import org.ice1000.tt.psi.minitt.MiniTTTypes
import org.ice1000.tt.psi.minitt.miniTTLexer

class MiniTTCommenter : TTCommenter() {
	override fun getLineCommentPrefix() = "-- "
}

class MiniTTBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(MiniTTTypes.LEFT_PAREN, MiniTTTypes.RIGHT_PAREN, false),
			BracePair(MiniTTTypes.LEFT_BRACE, MiniTTTypes.RIGHT_BRACE, false))
	}

	override fun getPairs() = PAIRS
}

class MiniTTFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(miniTTLexer(), MiniTTTokenType.IDENTIFIERS, MiniTTTokenType.COMMENTS, TokenSet.EMPTY)
}
