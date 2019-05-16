package org.ice1000.tt.editing.cubicaltt

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.cubicaltt.CubicalTTTokenType
import org.ice1000.tt.psi.cubicaltt.CubicalTTTypes
import org.ice1000.tt.psi.cubicaltt.cubicalTTLexer

class CubicalTTBraceMatcher : PairedBraceMatcher {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(CubicalTTTypes.LPAREN, CubicalTTTypes.RPAREN, false),
			BracePair(CubicalTTTypes.LBRACK, CubicalTTTypes.RBRACK, false),
			BracePair(CubicalTTTypes.LAYOUT_START, CubicalTTTypes.LAYOUT_END, false))
	}

	override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset
	override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true
	override fun getPairs() = PAIRS
}

class CubicalTTFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(cubicalTTLexer(), CubicalTTTokenType.IDENTIFIERS, CubicalTTTokenType.COMMENTS, TokenSet.EMPTY)
}
