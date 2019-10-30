/**
 * **No Agda parser.**
 * This file defines token types needed for Agda lexer.
 */
package org.ice1000.tt.psi.agda

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.AgdaLanguage
import org.ice1000.tt.psi.LayoutLexer
import org.ice1000.tt.psi.LetIn
import org.ice1000.tt.psi.State
import org.ice1000.tt.psi.agda.AgdaElementType.agdaLexer

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
		fun createLayout(text: String, project: Project) = fromText(text, project) as? AgdaLayout
		fun createSignature(text: String, project: Project) = createLayout(text, project)?.firstChild as? AgdaSignature
		fun createExp(text: String, project: Project) = createSignature("f : $text", project)?.exp
		fun createNameExp(text: String, project: Project) = createExp(text, project) as? AgdaNameExp
		fun createStr(text: String, project: Project) = createExp(text, project) as? AgdaString
	}
}

private val NON_CODE = TokenSet.orSet(
	AgdaTokenType.COMMENTS, AgdaTokenType.WHITE_SPACE,
	TokenSet.create(
		AgdaTypes.LAYOUT_START,
		AgdaTypes.LAYOUT_END,
		AgdaTypes.LAYOUT_SEP
	))
private val LAYOUT_CREATOR = TokenSet.create(
	AgdaTypes.KW_WHERE,
	AgdaTypes.KW_LET,
	AgdaTypes.KW_PRIMITIVE,
	AgdaTypes.KW_PRIVATE,
	AgdaTypes.KW_VARIABLE,
	AgdaTypes.KW_FIELD,
	AgdaTypes.KW_ABSTRACT,
	AgdaTypes.KW_MUTUAL,
	AgdaTypes.KW_POSTULATE
)

fun agdaLayoutLexer() = LayoutLexer(
	agdaLexer(),
	AgdaTokenType.EOL,
	AgdaTypes.LAYOUT_START,
	AgdaTypes.LAYOUT_SEP,
	AgdaTypes.LAYOUT_END,
	NON_CODE,
	LAYOUT_CREATOR,
	LetIn(AgdaTypes.KW_LET, AgdaTypes.KW_IN)
) { it: List<LayoutLexer.Token> ->
	if (it.any { it.elementType == AgdaTypes.KW_MODULE }) State.Normal
	else State.WaitingForLayout
}

class AgdaParserDefinition : AgdaGeneratedParserDefinition() {
	override fun getStringLiteralElements() = AgdaTokenType.STRINGS
	override fun createLexer(project: Project?) = agdaLayoutLexer()
	override fun getWhitespaceTokens() = AgdaTokenType.WHITE_SPACE
}
