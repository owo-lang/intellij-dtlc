package org.ice1000.tt.psi.acore

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
import org.ice1000.tt.ACoreFile
import org.ice1000.tt.ACoreLanguage
import org.ice1000.tt.psi.WHITE_SPACE

class ACoreElementType(debugName: String) : IElementType(debugName, ACoreLanguage.INSTANCE)

class ACoreTokenType(debugName: String) : IElementType(debugName, ACoreLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = ACoreTokenType("line comment")
		@JvmField val BLOCK_COMMENT = ACoreTokenType("block comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(ACoreTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(ACoreLanguage.INSTANCE, text).firstChild
		fun createDeclaration(text: String, project: Project) = fromText(text, project) as? ACoreDeclarationExpression
		fun createPattern(text: String, project: Project) = createDeclaration("let $text:U = 0;", project)?.declaration?.pattern
		fun createAtomPattern(text: String, project: Project) = createPattern(text, project) as? ACoreAtomPattern
		fun createVariable(text: String, project: Project) = createAtomPattern(text, project)?.variable
	}
}

fun acoreLexer() = FlexAdapter(ACoreLexer())

class ACoreParserDefinition : ParserDefinition {
	private companion object {
		private val FILE = IStubFileElementType<PsiFileStubImpl<ACoreFile>>(ACoreLanguage.INSTANCE)
	}

	override fun createParser(project: Project?) = ACoreParser()
	override fun createLexer(project: Project?) = acoreLexer()
	override fun createElement(node: ASTNode?): PsiElement = ACoreTypes.Factory.createElement(node)
	override fun createFile(viewProvider: FileViewProvider) = ACoreFile(viewProvider)
	override fun getStringLiteralElements() = TokenSet.EMPTY
	override fun getWhitespaceTokens() = WHITE_SPACE
	override fun getCommentTokens() = ACoreTokenType.COMMENTS
	override fun getFileNodeType() = FILE
	// TODO: replace after dropping support for 183
	override fun spaceExistanceTypeBetweenTokens(left: ASTNode?, right: ASTNode?) = ParserDefinition.SpaceRequirements.MAY
}
