package org.ice1000.tt.editing.minitt

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.MINI_TT_LINE_COMMENT
import org.ice1000.tt.editing.DefaultCommenter
import org.ice1000.tt.editing.DefaultFindUsagesProvider
import org.ice1000.tt.psi.minitt.MiniTTTokenType
import org.ice1000.tt.psi.minitt.MiniTTTypes
import org.ice1000.tt.psi.minitt.minittLexer

class MiniTTCommenter : DefaultCommenter() {
	override fun getLineCommentPrefix() = MINI_TT_LINE_COMMENT
}

class MiniTTBraceMatcher : PairedBraceMatcher {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(MiniTTTypes.LEFT_PAREN, MiniTTTypes.RIGHT_PAREN, false),
			BracePair(MiniTTTypes.LEFT_BRACE, MiniTTTypes.RIGHT_BRACE, false))
	}

	override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset
	override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true
	override fun getPairs() = PAIRS
}

class MiniTTFindUsagesProvider : DefaultFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(minittLexer(), MiniTTTokenType.IDENTIFIERS, MiniTTTokenType.COMMENTS, TokenSet.EMPTY)
}
