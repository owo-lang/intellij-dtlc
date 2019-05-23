package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language

fun LanguageUtilityGenerationTask.parser(configName: String, nickname: String) {
	@Language("kotlin")
	val parser = """
package $basePackage.psi.$nickname

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lexer.FlexAdapter
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IStubFileElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.${languageName}File
import org.ice1000.tt.${languageName}Language
import org.ice1000.tt.psi.WHITE_SPACE
import org.ice1000.tt.psi.$nickname.${languageName}Lexer
import org.ice1000.tt.psi.$nickname.${languageName}Parser
import org.ice1000.tt.psi.$nickname.${languageName}TokenType
import org.ice1000.tt.psi.$nickname.${languageName}Types

fun ${configName}Lexer() = FlexAdapter(${languageName}Lexer())

class ${languageName}ElementType(debugName: String)
 : IElementType(debugName, ${languageName}Language.INSTANCE)

open class ${languageName}GeneratedParserDefinition : ParserDefinition {
	private companion object {
		private val FILE = IStubFileElementType<PsiFileStubImpl<${languageName}File>>(${languageName}Language.INSTANCE)
	}

	override fun createParser(project: Project?) = ${languageName}Parser()
	override fun createLexer(project: Project?) = ${configName}Lexer()
	override fun createElement(node: ASTNode?): PsiElement = ${languageName}Types.Factory.createElement(node)
	override fun createFile(viewProvider: FileViewProvider) = ${languageName}File(viewProvider)
	override fun getStringLiteralElements() = TokenSet.EMPTY
	override fun getWhitespaceTokens() = WHITE_SPACE
	override fun getCommentTokens() = ${languageName}TokenType.COMMENTS
	override fun getFileNodeType() = FILE
	// TODO: replace after dropping support for 183
	override fun spaceExistanceTypeBetweenTokens(left: ASTNode?, right: ASTNode?) = ParserDefinition.SpaceRequirements.MAY
}

"""
	outDir.resolve("psi")
		.resolve(nickname)
		.apply { mkdirs() }
		.resolve("generated.kt")
		.apply { if (!exists()) createNewFile() }
		.apply { writeText(parser) }
}
