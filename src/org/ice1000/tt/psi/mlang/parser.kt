package org.ice1000.tt.psi.mlang

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.MlangLanguage

class MlangTokenType(debugName: String) : IElementType(debugName, MlangLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = MlangTokenType("line comment")
		@JvmField val BLOCK_COMMENT = MlangTokenType("block comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(MlangTypes.IDENTIFIER)
	}
}
