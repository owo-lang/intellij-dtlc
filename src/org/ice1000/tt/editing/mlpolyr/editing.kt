package org.ice1000.tt.editing.mlpolyr

import com.intellij.lang.BracePair
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.editing.TTCommenter
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.mlpolyr.MLPolyRTokenType
import org.ice1000.tt.psi.mlpolyr.MLPolyRTypes
import org.ice1000.tt.psi.mlpolyr.mlPolyRLexer

class MLPolyRCommenter : TTCommenter() {
	override fun getBlockCommentPrefix() = "(*"
	override fun getBlockCommentSuffix() = "*)"
	override fun getLineCommentPrefix(): String? = null
}

class MLPolyRBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(MLPolyRTypes.LP, MLPolyRTypes.RP, false),
			BracePair(MLPolyRTypes.LSB, MLPolyRTypes.RSB, false),
			BracePair(MLPolyRTypes.LCBB, MLPolyRTypes.RCBB, false),
			BracePair(MLPolyRTypes.LCB, MLPolyRTypes.RCB, false))
	}

	override fun getPairs() = PAIRS
}

class MLPolyRFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(mlPolyRLexer(), MLPolyRTokenType.IDENTIFIERS, MLPolyRTokenType.COMMENTS, MLPolyRTokenType.STRINGS)
}
