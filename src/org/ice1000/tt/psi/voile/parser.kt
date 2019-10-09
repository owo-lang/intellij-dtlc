package org.ice1000.tt.psi.voile

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.VoileLanguage

class VoileTokenType(debugName: String) : IElementType(debugName, VoileLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = VoileTokenType("line comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(VoileTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(VoileLanguage.INSTANCE, text).firstChild
		fun createImpl(text: String, project: Project) = fromText(text, project) as? VoileImplementation
		fun createNameDecl(text: String, project: Project) = createImpl("let $text = a;", project)?.nameDecl
		fun createExpr(text: String, project: Project) = createImpl("let n = $text;", project)?.expr
		fun createNameUsage(text: String, project: Project) = createExpr(text, project) as? VoileNameUsage
	}
}
