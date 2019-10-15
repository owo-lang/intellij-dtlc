package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language
import java.io.File

fun LangData.parser(configName: String, nickname: String, outDir: File) {
	val outPsiDir = outDir.resolve("psi").resolve(nickname)
	outPsiDir.mkdirs()

	val elementTypeClassName = "${languageName}ElementType"
	@Language("JAVA")
	val elementTypeClassContent = """
package $basePackage.psi.$nickname;

import com.intellij.lexer.FlexAdapter;
import com.intellij.psi.tree.IElementType;
import $basePackage.${languageName}Language;
import org.jetbrains.annotations.NotNull;

public class $elementTypeClassName extends IElementType {
	public $elementTypeClassName(@NotNull String debugName) {
		super(debugName, ${languageName}Language.INSTANCE);
	}

	public static @NotNull FlexAdapter ${configName}Lexer() {
		return new FlexAdapter(new ${languageName}Lexer());
	}
}"""
	outPsiDir.resolve("$elementTypeClassName.java").writeText(elementTypeClassContent)

	val parserDefClassName = "${languageName}GeneratedParserDefinition"
	@Language("JAVA")
	val parserDefClassContent = """
package $basePackage.psi.$nickname;

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
import $basePackage.${languageName}File;
import $basePackage.${languageName}Language;
import org.jetbrains.annotations.NotNull;

public class $parserDefClassName implements ParserDefinition {
	private static @NotNull IStubFileElementType<PsiFileStubImpl<${languageName}File>> FILE = new IStubFileElementType<>(${languageName}Language.INSTANCE);

	public @NotNull @Override Lexer createLexer(Project project) { return $elementTypeClassName.${configName}Lexer(); }
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
