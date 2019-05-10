package org.ice1000.tt.psi.redprl

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lexer.FlexAdapter
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IStubFileElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.RedPrlFile
import org.ice1000.tt.RedPrlLanguage
import org.ice1000.tt.psi.WHITE_SPACE

class RedPrlElementType(debugName: String) : IElementType(debugName, RedPrlLanguage.INSTANCE)

class RedPrlTokenType(debugName: String) : IElementType(debugName, RedPrlLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = RedPrlTokenType("line comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(TODO(/*RedPrlTypes.IDENTIFIER*/))

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(RedPrlLanguage.INSTANCE, text).firstChild
	}
}

fun redPrlLexer() = FlexAdapter(TODO(/*RedPrlLexer()*/))

class RedPrlParserDefinition : ParserDefinition {
	private companion object {
		private val FILE = IStubFileElementType<PsiFileStubImpl<RedPrlFile>>(RedPrlLanguage.INSTANCE)
	}

	override fun createParser(project: Project?) = TODO(/*RedPrlParser()*/)
	override fun createLexer(project: Project?) = redPrlLexer()
	override fun createElement(node: ASTNode?): PsiElement = TODO(/*RedPrlTypes.Factory.createElement(node)*/)
	override fun createFile(viewProvider: FileViewProvider) = RedPrlFile(viewProvider)
	override fun getStringLiteralElements() = TokenSet.EMPTY
	override fun getWhitespaceTokens() = WHITE_SPACE
	override fun getCommentTokens() = RedPrlTokenType.COMMENTS
	override fun getFileNodeType() = FILE
	// TODO: replace after dropping support for 183
	override fun spaceExistanceTypeBetweenTokens(left: ASTNode?, right: ASTNode?) = ParserDefinition.SpaceRequirements.MAY
}
