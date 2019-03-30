package org.ice1000.tt.psi.minitt

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lexer.FlexAdapter
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.TokenType
import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IStubFileElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.MiniTTFile
import org.ice1000.tt.MiniTTLanguage

class MiniTTElementType(debugName: String) : IElementType(debugName, MiniTTLanguage.INSTANCE)

class MiniTTTokenType(debugName: String) : IElementType(debugName, MiniTTLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = MiniTTTokenType("line comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT)
		@JvmField val WHITE_SPACE = TokenSet.create(TokenType.WHITE_SPACE)
		@JvmField val IDENTIFIERS = TokenSet.create(MiniTTTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(MiniTTLanguage.INSTANCE, text).firstChild
		fun createConst(text: String, project: Project) = fromText(text, project) as? MiniTTConstExpression
		fun createPattern(text: String, project: Project) = createConst("const $text = 0;", project)?.constDeclaration
		fun createAtomPattern(text: String, project: Project) = createPattern(text, project) as? MiniTTAtomPattern
		fun createVariable(text: String, project: Project) = createAtomPattern(text, project)?.variable
	}
}

fun minittLexer() = FlexAdapter(MiniTTLexer())

class MiniTTParserDefinition : ParserDefinition {
	private companion object {
		private val FILE = IStubFileElementType<PsiFileStubImpl<MiniTTFile>>(MiniTTLanguage.INSTANCE)
	}

	override fun createParser(project: Project?) = MiniTTParser()
	override fun createLexer(project: Project?) = minittLexer()
	override fun createElement(node: ASTNode?): PsiElement = MiniTTTypes.Factory.createElement(node)
	override fun createFile(viewProvider: FileViewProvider) = MiniTTFile(viewProvider)
	override fun getStringLiteralElements() = TokenSet.EMPTY
	override fun getWhitespaceTokens() = MiniTTTokenType.WHITE_SPACE
	override fun getCommentTokens() = MiniTTTokenType.COMMENTS
	override fun getFileNodeType() = FILE
	// TODO: replace after dropping support for 183
	override fun spaceExistanceTypeBetweenTokens(left: ASTNode?, right: ASTNode?) = ParserDefinition.SpaceRequirements.MAY
}
