package org.ice1000.tt.psi.voile

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.VoileLanguage

class VoileTokenType(debugName: String) : IElementType(debugName, VoileLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = VoileTokenType("line comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT)
	}
}

