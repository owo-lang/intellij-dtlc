package org.ice1000.tt.editing.voile

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.editing.TTCommenter
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.redprl.RedPrlTokenType
import org.ice1000.tt.psi.redprl.RedPrlTypes
import org.ice1000.tt.psi.redprl.redPrlLexer
import org.ice1000.tt.psi.voile.VoileTokenType
import org.ice1000.tt.psi.voile.VoileTypes

class VoileCommenter : TTCommenter() {
	override fun getLineCommentPrefix() = "// "
}

class VoileBraceMatcher : PairedBraceMatcher {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(VoileTypes.LPAREN, VoileTypes.RPAREN, false))
	}

	override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset
	override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true
	override fun getPairs() = PAIRS
}

class VoileFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(redPrlLexer(), VoileTokenType.IDENTIFIERS, VoileTokenType.COMMENTS, TokenSet.EMPTY)
}
