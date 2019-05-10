package org.ice1000.tt.editing.mlpolyr

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.editing.DefaultCommenter
import org.ice1000.tt.editing.DefaultFindUsagesProvider
import org.ice1000.tt.psi.mlpolyr.MLPolyRTokenType
import org.ice1000.tt.psi.mlpolyr.MLPolyRTypes
import org.ice1000.tt.psi.mlpolyr.mlPolyRLexer

class MLPolyRCommenter : DefaultCommenter() {
	override fun getBlockCommentPrefix() = "(*"
	override fun getBlockCommentSuffix() = "*)"
	override fun getLineCommentPrefix(): String? = null
}

class MLPolyRBraceMatcher : PairedBraceMatcher {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(MLPolyRTypes.LP, MLPolyRTypes.RP, false),
			BracePair(MLPolyRTypes.LSB, MLPolyRTypes.RSB, false),
			BracePair(MLPolyRTypes.LCBB, MLPolyRTypes.RCBB, false),
			BracePair(MLPolyRTypes.LCB, MLPolyRTypes.RCB, false))
	}

	override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset
	override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true
	override fun getPairs() = PAIRS
}

class MLPolyRFindUsagesProvider : DefaultFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(mlPolyRLexer(), MLPolyRTokenType.IDENTIFIERS, MLPolyRTokenType.COMMENTS, MLPolyRTokenType.STRINGS)
}
