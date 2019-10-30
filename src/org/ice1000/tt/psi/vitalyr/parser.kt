package org.ice1000.tt.psi.vitalyr

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.VitalyRLanguage

class VitalyRTokenType(debugName: String) : IElementType(debugName, VitalyRLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = VitalyRTokenType("line comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(VitalyRTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(VitalyRLanguage.INSTANCE, text).firstChild
	}
}
