package org.ice1000.tt.psi.yacctt

import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.YaccTTFile
import org.ice1000.tt.YaccTTLanguage
import org.ice1000.tt.psi.LayoutLexer
import org.ice1000.tt.psi.LetIn
import org.ice1000.tt.psi.childrenWithLeaves
import org.ice1000.tt.psi.yacctt.YaccTTElementType.yaccttLexer

class YaccTTFileImpl(viewProvider: FileViewProvider) : YaccTTFile(viewProvider), PsiNameIdentifierOwner {
	val module: YaccTTModule? = childrenWithLeaves.filterIsInstance<YaccTTModule>().firstOrNull()
	override fun getNameIdentifier() = module?.nameDecl
}

class YaccTTTokenType(debugName: String) : IElementType(debugName, YaccTTLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = YaccTTTokenType("line comment")
		@JvmField val BLOCK_COMMENT = YaccTTTokenType("block comment")
		@JvmField val EOL = YaccTTTokenType("eol")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)
		@JvmField val IDENTIFIERS = TokenSet.create(YaccTTTypes.IDENTIFIER)
		@JvmField val WHITE_SPACE = TokenSet.create(EOL, TokenType.WHITE_SPACE)

		fun fromText(text: String, project: Project) = PsiFileFactory.getInstance(project).createFileFromText(YaccTTLanguage.INSTANCE, text).firstChild
		fun createExp(text: String, project: Project) = fromText(text, project) as? YaccTTExp
		fun createNameExp(text: String, project: Project) = createExp(text, project) as? YaccTTNameExp
		fun createLamExp(text: String, project: Project) = createExp(text, project) as? YaccTTLamExp
		fun createTele(text: String, project: Project) = createLamExp("\\$text->", project)?.teleList?.firstOrNull()
		fun createNameDecl(text: String, project: Project) = createTele("($text:a)", project)?.nameDeclList?.firstOrNull()
	}
}

private val NON_CODE = TokenSet.orSet(
	YaccTTTokenType.COMMENTS,
	YaccTTTokenType.WHITE_SPACE,
	TokenSet.create(
		YaccTTTypes.LAYOUT_START,
		YaccTTTypes.LAYOUT_SEP,
		YaccTTTypes.LAYOUT_END
	))
private val LAYOUT_CREATOR = TokenSet.create(
	YaccTTTypes.KW_LET,
	YaccTTTypes.KW_MUTUAL,
	YaccTTTypes.KW_SPLIT,
	YaccTTTypes.KW_WHERE,
	YaccTTTypes.KW_WITH
)

fun yaccTTLayoutLexer() = LayoutLexer(
	yaccttLexer(),
	YaccTTTokenType.EOL,
	YaccTTTypes.LAYOUT_START,
	YaccTTTypes.LAYOUT_SEP,
	YaccTTTypes.LAYOUT_END,
	NON_CODE,
	LAYOUT_CREATOR,
	LetIn(YaccTTTypes.KW_LET, YaccTTTypes.KW_IN)
)

class YaccTTParserDefinition : YaccTTGeneratedParserDefinition() {
	override fun createFile(viewProvider: FileViewProvider) = YaccTTFileImpl(viewProvider)
	override fun createLexer(project: Project?) = yaccTTLayoutLexer()
	override fun getFileNodeType(): IFileElementType = YaccTTFileStub.Type
	override fun getWhitespaceTokens() = YaccTTTokenType.WHITE_SPACE
}
