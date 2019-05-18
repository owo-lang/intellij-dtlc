package org.ice1000.tt.editing.redprl

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.redprl.RedPrlTokenType
import org.ice1000.tt.psi.redprl.RedPrlTypes
import org.ice1000.tt.psi.redprl.redPrlLexer

class RedPrlBraceMatcher : PairedBraceMatcher {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(RedPrlTypes.LPAREN, RedPrlTypes.RPAREN, false),
			BracePair(RedPrlTypes.LSQUARE, RedPrlTypes.RSQUARE, false),
			BracePair(RedPrlTypes.LBRACKET, RedPrlTypes.RBRACKET, false))
	}

	override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset
	override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true
	override fun getPairs() = PAIRS
}

class RedPrlFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(redPrlLexer(), RedPrlTokenType.IDENTIFIERS, RedPrlTokenType.COMMENTS, TokenSet.EMPTY)
}
