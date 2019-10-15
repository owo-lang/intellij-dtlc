package org.ice1000.tt.editing.redprl

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.redprl.RedPrlElementType.redprlLexer
import org.ice1000.tt.psi.redprl.RedPrlTokenType

class RedPrlFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(redprlLexer(), RedPrlTokenType.IDENTIFIERS, RedPrlTokenType.COMMENTS, TokenSet.EMPTY)
}
