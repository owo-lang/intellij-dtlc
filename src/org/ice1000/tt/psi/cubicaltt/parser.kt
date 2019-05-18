package org.ice1000.tt.psi.cubicaltt

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lexer.FlexAdapter
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.CubicalTTFile
import org.ice1000.tt.CubicalTTLanguage
import org.ice1000.tt.psi.LayoutLexer
import org.ice1000.tt.psi.LetIn
import org.ice1000.tt.psi.childrenWithLeaves

class CubicalTTFileImpl(viewProvider: FileViewProvider) : CubicalTTFile(viewProvider), PsiNameIdentifierOwner {
	val module: CubicalTTModule? = childrenWithLeaves.filterIsInstance<CubicalTTModule>().firstOrNull()
	override fun getNameIdentifier() = module?.nameDecl
}

class CubicalTTElementType(debugName: String) : IElementType(debugName, CubicalTTLanguage.INSTANCE)
class CubicalTTTokenType(debugName: String) : IElementType(debugName, CubicalTTLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = CubicalTTTokenType("line comment")
		@JvmField val BLOCK_COMMENT = CubicalTTTokenType("block comment")
		@JvmField val EOL = CubicalTTTokenType("eol")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(CubicalTTTypes.IDENTIFIER)
		@JvmField val WHITE_SPACE = TokenSet.create(EOL, TokenType.WHITE_SPACE)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(CubicalTTLanguage.INSTANCE, text).firstChild
		fun createExp(text: String, project: Project) = fromText(text, project) as? CubicalTTExp
		fun createNameExp(text: String, project: Project) = createExp(text, project) as? CubicalTTNameExp
		fun createLamExp(text: String, project: Project) = createExp(text, project) as? CubicalTTLamExp
		fun createTele(text: String, project: Project) = createLamExp("\\$text->", project)?.teleList?.firstOrNull()
		fun createNameDecl(text: String, project: Project) = createTele("($text:a)", project)?.nameDeclList?.firstOrNull()
	}
}

private val NON_CODE = TokenSet.orSet(
	CubicalTTTokenType.COMMENTS,
	CubicalTTTokenType.WHITE_SPACE,
	TokenSet.create(
		CubicalTTTypes.LAYOUT_START,
		CubicalTTTypes.LAYOUT_SEP,
		CubicalTTTypes.LAYOUT_END
	))
private val LAYOUT_CREATOR = TokenSet.create(
	CubicalTTTypes.KW_LET,
	CubicalTTTypes.KW_MUTUAL,
	CubicalTTTypes.KW_SPLIT,
	CubicalTTTypes.KW_WHERE,
	CubicalTTTypes.KW_WITH
)

fun cubicalTTLexer() = FlexAdapter(CubicalTTLexer())
fun cubicalTTLayoutLexer() = LayoutLexer(
	cubicalTTLexer(),
	CubicalTTTokenType.EOL,
	CubicalTTTypes.LAYOUT_START,
	CubicalTTTypes.LAYOUT_SEP,
	CubicalTTTypes.LAYOUT_END,
	NON_CODE,
	LAYOUT_CREATOR,
	LetIn(CubicalTTTypes.KW_LET, CubicalTTTypes.KW_IN)
)

class CubicalTTParserDefinition : ParserDefinition {
	override fun getStringLiteralElements() = TokenSet.EMPTY
	override fun getCommentTokens() = CubicalTTTokenType.COMMENTS
	override fun createElement(node: ASTNode?) = CubicalTTTypes.Factory.createElement(node)
	override fun createFile(viewProvider: FileViewProvider) = CubicalTTFileImpl(viewProvider)
	override fun createLexer(project: Project?) = cubicalTTLayoutLexer()
	override fun createParser(project: Project?) = CubicalTTParser()
	override fun getFileNodeType() = CubicalTTFileStub.Type
	override fun getWhitespaceTokens() = CubicalTTTokenType.WHITE_SPACE
	// TODO: replace after dropping support for 183
	override fun spaceExistanceTypeBetweenTokens(left: ASTNode?, right: ASTNode?) = ParserDefinition.SpaceRequirements.MAY
}
