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
import com.intellij.psi.TokenType
import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IStubFileElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.AgdaFile
import org.ice1000.tt.AgdaLanguage
import org.ice1000.tt.psi.LayoutLexer

class AgdaElementType(debugName: String) : IElementType(debugName, AgdaLanguage.INSTANCE)

class AgdaTokenType(debugName: String) : IElementType(debugName, AgdaLanguage.INSTANCE) {
	@Suppress("MemberVisibilityCanBePrivate")
	companion object Builtin {
		@JvmField val LINE_COMMENT = AgdaTokenType("line comment")
		@JvmField val PRAGMA = AgdaTokenType("{-# #-}")
		@JvmField val EOL = AgdaTokenType("eol")
		@JvmField val BLOCK_COMMENT = AgdaTokenType("block comment")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT, PRAGMA)
		@JvmField val WHITE_SPACE = TokenSet.create(EOL, TokenType.WHITE_SPACE)
		@JvmField val STRINGS = TokenSet.create(AgdaTypes.CHR_LIT, AgdaTypes.STR_LIT)
		@JvmField val IDENTIFIERS = TokenSet.create(AgdaTypes.IDENTIFIER)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(AgdaLanguage.INSTANCE, text)?.firstChild
		fun createSignature(text: String, project: Project) = fromText(text, project) as? AgdaSignature
		fun createExp(text: String, project: Project) = createSignature("f : $text", project)?.exp
		fun createStr(text: String, project: Project) = createExp(text, project) as? AgdaString
	}
}

@JvmField val NON_CODE = TokenSet.orSet(
	AgdaTokenType.COMMENTS, AgdaTokenType.WHITE_SPACE,
	TokenSet.create(
		AgdaTypes.LAYOUT_START,
		AgdaTypes.LAYOUT_END,
		AgdaTypes.LAYOUT_SEP
	))
@JvmField val LAYOUT_CREATOR = TokenSet.create(
	AgdaTypes.KW_WHERE,
	AgdaTypes.KW_PRIMITIVE,
	AgdaTypes.KW_PRIVATE,
	AgdaTypes.KW_VARIABLE,
	AgdaTypes.KW_FIELD,
	AgdaTypes.KW_ABSTRACT,
	AgdaTypes.KW_MUTUAL,
	AgdaTypes.KW_POSTULATE
)

fun agdaLexer() = FlexAdapter(AgdaLexer())
fun agdaLayoutLexer() = LayoutLexer(
	agdaLexer(),
	AgdaTokenType.EOL,
	AgdaTypes.LAYOUT_START,
	AgdaTypes.LAYOUT_SEP,
	AgdaTypes.LAYOUT_END,
	NON_CODE,
	LAYOUT_CREATOR
)

class AgdaParserDefinition : ParserDefinition {
	private companion object {
		private val FILE = IStubFileElementType<PsiFileStubImpl<AgdaFile>>(AgdaLanguage.INSTANCE)
	}

	override fun getStringLiteralElements() = AgdaTokenType.STRINGS
	override fun getCommentTokens() = AgdaTokenType.COMMENTS
	override fun createElement(node: ASTNode?) = AgdaTypes.Factory.createElement(node)
	override fun createFile(viewProvider: FileViewProvider) = AgdaFile(viewProvider)
	override fun createLexer(project: Project?) = agdaLayoutLexer()
	override fun createParser(project: Project?) = AgdaParser()
	override fun getFileNodeType() = FILE
	override fun getWhitespaceTokens() = AgdaTokenType.WHITE_SPACE
	// TODO: replace after dropping support for 183
	override fun spaceExistanceTypeBetweenTokens(left: ASTNode?, right: ASTNode?) = ParserDefinition.SpaceRequirements.MAY
}
