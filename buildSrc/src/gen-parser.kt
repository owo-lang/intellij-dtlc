package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language

fun LanguageUtilityGenerationTask.parser(configName: String, nickname: String) {
	@Language("kotlin")
	val lexerAndElementType = """
package $basePackage.psi.$nickname

import com.intellij.lexer.FlexAdapter
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.${languageName}Language

fun ${configName}Lexer() = FlexAdapter(${languageName}Lexer())

class ${languageName}ElementType(debugName: String)
 : IElementType(debugName, ${languageName}Language.INSTANCE)
"""
	val outPsiDir = outDir.resolve("psi").resolve(nickname)
	outPsiDir.mkdirs()
	outPsiDir.resolve("generated.kt").writeText(lexerAndElementType)

	val parserDefClassName = "${languageName}GeneratedParserDefinition"
	@Language("JAVA")
	val parserDefClassContent = """
package org.ice1000.tt.psi.$nickname;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.ice1000.tt.${languageName}File;
import org.ice1000.tt.${languageName}Language;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.tt.psi.$nickname.GeneratedKt.${configName}Lexer;

public class $parserDefClassName implements ParserDefinition {
	private static @NotNull IStubFileElementType<PsiFileStubImpl<${languageName}File>> FILE = new IStubFileElementType<>(${languageName}Language.INSTANCE);

	public @NotNull @Override Lexer createLexer(Project project) { return ${configName}Lexer(); }
	public @Override PsiParser createParser(Project project) { return new ${languageName}Parser(); }
	public @Override IFileElementType getFileNodeType() { return FILE; }
	public @NotNull @Override TokenSet getCommentTokens() { return ${languageName}TokenType.COMMENTS; }
	public @NotNull @Override TokenSet getStringLiteralElements() { return TokenSet.EMPTY; }
	public @NotNull @Override PsiElement createElement(ASTNode astNode) { return ${languageName}Types.Factory.createElement(astNode); }
	public @Override PsiFile createFile(FileViewProvider fileViewProvider) { return new ${languageName}File(fileViewProvider); }
	public @Override SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
		return SpaceRequirements.MAY;
	}
}"""
	outPsiDir.resolve("$parserDefClassName.java").writeText(parserDefClassContent)
}
