package org.ice1000.tt.psi.redprl

import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IStubFileElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.RedPrlFile
import org.ice1000.tt.RedPrlLanguage
import org.ice1000.tt.psi.childrenRevWithLeaves

class RedPrlFileImpl(viewProvider: FileViewProvider) : RedPrlFile(viewProvider) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		childrenRevWithLeaves.all { it.processDeclarations(processor, state, lastParent, place) }
}

class RedPrlTokenType(debugName: String) : IElementType(debugName, RedPrlLanguage.INSTANCE) {
	@Suppress("MemberVisibilityCanBePrivate")
	companion object Builtin {
		@JvmField val LINE_COMMENT = RedPrlTokenType("line comment")
		@JvmField val BLOCK_COMMENT = RedPrlTokenType("block comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(RedPrlTypes.OPNAME, RedPrlTypes.VARNAME, RedPrlTypes.HOLENAME)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(RedPrlLanguage.INSTANCE, text).firstChild
		fun createDecl(text: String, project: Project) = fromText("$text.", project) as? RedPrlMlDecl
		fun createDefine(text: String, project: Project) = createDecl(text, project) as? RedPrlMlDeclDef
		fun createTactic(text: String, project: Project) = createDecl(text, project) as? RedPrlMlDeclTactic
		fun createVal(text: String, project: Project) = createDecl(text, project) as? RedPrlMlDeclVal
		fun createOpDecl(text: String, project: Project) = createVal("val $text = quit", project)?.opDecl
		fun createCmd(text: String, project: Project) = createVal("val Op = $text", project)?.mlCmd
		fun createOpUsage(text: String, project: Project) = createCmd(text, project)?.firstChild as? RedPrlOpUsage
		fun createTerm(text: String, project: Project) = createDefine("define Bla = $text.", project)?.term
		fun createDeclArgs(text: String, project: Project) = createDefine("define Bla $text = quit.", project)?.declArgumentsParens
		fun createDeclArg(text: String, project: Project) = createDeclArgs("($text)", project)?.declArgumentList?.firstOrNull()
		fun createMetaDecl(text: String, project: Project) = createDeclArg(text, project)?.metaDecl
		fun createTermAndTac(text: String, project: Project) = createTerm(text, project)?.termAndTac
		fun createVarUsage(text: String, project: Project) = createTermAndTac(text, project) as? RedPrlVarUsage
		fun createMetaUsage(text: String, project: Project) = createTermAndTac(text, project) as? RedPrlMetaUsage
		fun createMultitac(text: String, project: Project) = createTactic("tactic Bla = {$text}", project)?.tactic as? RedPrlAtomicMultitac
		fun createTac(text: String, project: Project) = createMultitac(text, project)?.tac
		fun createQueryTac(text: String, project: Project) = createTac(text, project) as? RedPrlQueryTac
		fun createVarDecl(text: String, project: Project) = createQueryTac("query $text <-", project)?.varDecl
		fun createBoundVar(text: String, project: Project) = createTermAndTac("(abs bla [$text] Bla)", project)?.boundVarList?.firstOrNull()
	}
}

class RedPrlParserDefinition : RedPrlGeneratedParserDefinition() {
	private companion object {
		private val FILE = IStubFileElementType<PsiFileStubImpl<RedPrlFileImpl>>(RedPrlLanguage.INSTANCE)
	}

	override fun createFile(viewProvider: FileViewProvider) = RedPrlFileImpl(viewProvider)
	override fun getFileNodeType() = FILE
}
