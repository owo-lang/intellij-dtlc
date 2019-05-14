/**
 * **No Agda parser.**
 * This file defines token types needed for Agda lexer.
 */
package org.ice1000.tt.psi.agda

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lexer.FlexAdapter
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IStubFileElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.AgdaFile
import org.ice1000.tt.AgdaLanguage
import org.ice1000.tt.psi.agda.AgdaTypes.*

class AgdaElementType(debugName: String) : IElementType(debugName, AgdaLanguage.INSTANCE)

class AgdaTokenType(debugName: String) : IElementType(debugName, AgdaLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = AgdaTokenType("line comment")
		@JvmField val PRAGMA = AgdaTokenType("{-# #-}")
		@JvmField val BLOCK_COMMENT = AgdaTokenType("block comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT, HOLE, PRAGMA)
		@JvmField val STRINGS = TokenSet.create(CHR_LIT, STR_LIT)
		@JvmField val IDENTIFIERS = TokenSet.create(IDENTIFIER)

		@JvmStatic fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(AgdaLanguage.INSTANCE, text)?.firstChild
		@JvmStatic fun createStr(text: String, project: Project) = fromText(text, project) as? AgdaString
	}
}

fun agdaLexer() = FlexAdapter(AgdaLexer())

class AgdaParserDefinition : ParserDefinition {
	private companion object {
		private val FILE = IStubFileElementType<PsiFileStubImpl<AgdaFile>>(AgdaLanguage.INSTANCE)
	}

	override fun getStringLiteralElements() = AgdaTokenType.STRINGS
	override fun getCommentTokens() = AgdaTokenType.COMMENTS
	override fun createElement(node: ASTNode?) = Factory.createElement(node)
	override fun createFile(viewProvider: FileViewProvider) = AgdaFile(viewProvider)
	override fun createLexer(project: Project?) = agdaLexer()
	override fun createParser(project: Project?) = AgdaParser()
	override fun getFileNodeType() = FILE
	// TODO: replace after dropping support for 183
	override fun spaceExistanceTypeBetweenTokens(left: ASTNode?, right: ASTNode?) = ParserDefinition.SpaceRequirements.MAY
}
