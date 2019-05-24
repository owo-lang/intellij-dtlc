package org.ice1000.tt.psi.mlpolyr

import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IStubFileElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.MLPolyRFile
import org.ice1000.tt.MLPolyRLanguage

class MLPolyRFileImpl(viewProvider: FileViewProvider) : MLPolyRFile(viewProvider) {
	private val consCache = HashSet<MLPolyRLabelMixin>(50)
	fun addConstructor(element: MLPolyRLabelMixin) = consCache.add(element)

	fun <T> useConstructors(f: Collection<MLPolyRLabelMixin>.() -> T): T = synchronized(consCache) {
		consCache.retainAll { it.isValid }
		consCache.f()
	}
}

class MLPolyRTokenType(debugName: String) : IElementType(debugName, MLPolyRLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val COMMENT = MLPolyRTokenType("comment")
		@JvmField val COMMENTS = TokenSet.create(COMMENT)
		@JvmField val STRINGS = TokenSet.create(MLPolyRTypes.STR, MLPolyRTypes.STRING, COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(MLPolyRTypes.ID, MLPolyRTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(MLPolyRLanguage.INSTANCE, text).firstChild
		fun createExp(text: String, project: Project) = fromText(text, project) as? MLPolyRExp
		fun createLet(text: String, project: Project) = createExp(text, project) as? MLPolyRLetExp
		fun createDef(text: String, project: Project) = createLet("let $text in 0 end", project)?.defList?.firstOrNull()
		fun createPat(text: String, project: Project) = createDef("val $text = 0", project)?.pat
		fun createIdentifier(text: String, project: Project) = createExp(text, project)?.firstChild as? MLPolyRIdentifier
		fun createStr(text: String, project: Project) = createExp(text, project)?.firstChild as? MLPolyRStringMixin
		fun createCon(text: String, project: Project) = createExp("$text 0", project)?.firstChild as? MLPolyRCon
		fun createLabel(text: String, project: Project) = createCon("`$text", project)?.label
	}
}

class MLPolyRParserDefinition : MLPolyRGeneratedParserDefinition() {
	private companion object {
		private val FILE = IStubFileElementType<PsiFileStubImpl<MLPolyRFileImpl>>(MLPolyRLanguage.INSTANCE)
	}

	override fun createFile(viewProvider: FileViewProvider) = MLPolyRFileImpl(viewProvider)
	override fun getFileNodeType() = FILE
}
