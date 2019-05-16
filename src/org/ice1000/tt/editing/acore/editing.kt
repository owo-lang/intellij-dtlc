package org.ice1000.tt.editing.acore

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.acore.ACoreTokenType
import org.ice1000.tt.psi.acore.ACoreTypes
import org.ice1000.tt.psi.acore.acoreLexer

class ACoreBraceMatcher : PairedBraceMatcher {
	private companion object Pairs {
		private val PAIRS = arrayOf(BracePair(ACoreTypes.LEFT_PAREN, ACoreTypes.RIGHT_PAREN, false))
	}

	override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset
	override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true
	override fun getPairs() = PAIRS
}

class ACoreFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(acoreLexer(), ACoreTokenType.IDENTIFIERS, ACoreTokenType.COMMENTS, TokenSet.EMPTY)
}

