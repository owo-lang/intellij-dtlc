package org.ice1000.tt.psi.minitt

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.MiniTTLanguage

class MiniTTTokenType(debugName: String) : IElementType(debugName, MiniTTLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = MiniTTTokenType("line comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(MiniTTTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(MiniTTLanguage.INSTANCE, text).firstChild
		fun createConst(text: String, project: Project) = fromText(text, project) as? MiniTTConstExpression
		fun createPattern(text: String, project: Project) = createConst("const $text = 0;", project)?.constDeclaration?.pattern
		fun createAtomPattern(text: String, project: Project) = createPattern(text, project) as? MiniTTAtomPattern
		fun createVariable(text: String, project: Project) = createAtomPattern(text, project)?.variable
	}
}
