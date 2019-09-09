package org.ice1000.tt.psi.mlang

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.MlangLanguage
import org.ice1000.tt.psi.childrenWithLeaves

class MlangTokenType(debugName: String) : IElementType(debugName, MlangLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = MlangTokenType("line comment")
		@JvmField val BLOCK_COMMENT = MlangTokenType("block comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(MlangTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(MlangLanguage.INSTANCE, text).firstChild
		fun createDefine(text: String, project: Project) = fromText(text, project) as? MlangDefine
		fun createTerm(text: String, project: Project) = createDefine("define a = $text", project)?.lastChild as? MlangTerm
		fun createIdent(text: String, project: Project) = createDefine("define $text = 0", project)?.ident
		fun createTele(text: String, project: Project) = createDefine("define a$text = 0", project)?.tele
		fun createParam(text: String, project: Project) = createTele("($text)", project)?.childrenWithLeaves?.filterIsInstance<MlangParam>()?.firstOrNull()
		fun createParamIdent(text: String, project: Project) = createParam("$text:a", project)?.childrenWithLeaves?.filterIsInstance<MlangParamIdent>()?.firstOrNull()
		fun createParamIdentExpl(text: String, project: Project) = createParamIdent(text, project)?.firstChild
		fun createRefExpr(text: String, project: Project) = createTerm(text, project) as? MlangRefExpr
	}
}
