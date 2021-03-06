package org.ice1000.tt.gradle

import org.ice1000.tt.gradle.json.LangData
import org.intellij.lang.annotations.Language
import java.io.File

fun LangData.declarationDefaultName(nickname: String, outDir: File) {
	if (declarationDefaultIdentifierName.isEmpty()) return
	val outPsiDir = dir(outDir, "psi", nickname)
	val prefix = "$languageName$declarationDefaultIdentifierName"
	val declTypeClassName = "${prefix}GeneratedMixin"
	@Language("JAVA")
	val declTypeClassContent = """
package $basePackage.psi.$nickname;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import icons.TTIcons;
import icons.SemanticIcons;
import org.ice1000.tt.psi.UtilsKt;
import org.ice1000.tt.psi.GeneralNameIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static org.ice1000.tt.psi.UtilsKt.invalidName;

public abstract class $declTypeClassName extends GeneralNameIdentifier implements $prefix {
	public $declTypeClassName(@NotNull ASTNode node) { super(node); }
	@Override public @Nullable Icon getIcon(int flags) { return SemanticIcons.BLUE_HOLE; }

	@Override
	public @NotNull PsiElement setName(@NotNull String s) throws IncorrectOperationException {
		PsiElement nameIdentifier = getNameIdentifier();
		if (nameIdentifier != null) {
			PsiElement element = ${languageName}TokenType.Builtin.create$declarationDefaultIdentifierName(s, getProject());
			if (element == null) invalidName(s);
			nameIdentifier.replace(element);
		}
		return this;
	}
}"""
	outPsiDir.resolve("$declTypeClassName.java").writeText(declTypeClassContent)
}

fun LangData.declarationMixins(nickname: String, outDir: File) {
	val outPsiDir = dir(outDir, "psi", nickname)
	declarationTypes.forEach { decl ->
		val prefix = "$languageName${decl.name}"
		val declTypeClassName = "${prefix}GeneratedMixin"
		val identifierName = decl.identifierName ?: declarationDefaultIdentifierName
		val findType = decl.findType ?: declarationDefaultFindType
		@Language("JAVA")
		val declTypeClassContent = """
package $basePackage.psi.$nickname;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import icons.TTIcons;
import icons.SemanticIcons;
import org.ice1000.tt.psi.UtilsKt;
import org.ice1000.tt.psi.GeneralDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static org.ice1000.tt.psi.UtilsKt.invalidName;

public abstract class $declTypeClassName extends GeneralDeclaration {
	public $declTypeClassName(@NotNull ASTNode node) { super(node); }
	@Override public @Nullable PsiElement getType() { return $findType; }
	@Override public @Nullable Icon getIcon(int flags) { return ${decl.icon}; }

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		return findChildByClass($languageName$identifierName.class);
	}

	@Override
	public @NotNull PsiElement setName(@NotNull String s) throws IncorrectOperationException {
		PsiElement element = ${languageName}TokenType.Builtin.create$identifierName(s, getProject());
		if (element == null) invalidName(s);
		PsiElement nameIdentifier = getNameIdentifier();
		if (nameIdentifier == null) throw new IncorrectOperationException("Cannot rename!");
		return nameIdentifier.replace(element);
	}
}"""
		outPsiDir.resolve("$declTypeClassName.java").writeText(declTypeClassContent)
	}
}

fun LangData.rename(prefix: String, name: String) = """@Override
	public PsiElement handleElementRename(@NotNull String s) throws IncorrectOperationException {
		$prefix element = ${languageName}TokenType.Builtin.create$name(s, getProject());
		if (element == null) invalidName(s);
		return replace(element);
	}
"""

fun variants(completion: String) = """@Override
	public @NotNull Object[] getVariants() {
		return resolveWithToList($completion, this)
			.toArray(LookupElement.EMPTY_ARRAY);
	}
"""
fun multiResolve(resolver: String) = """@Override
	public @NotNull ResolveResult[] multiResolve(boolean incomplete) {
		PsiFile containingFile = getContainingFile();
		if (containingFile == null) return ResolveResult.EMPTY_ARRAY;
		if (!isValid() || getProject().isDisposed()) return ResolveResult.EMPTY_ARRAY;
		return ResolveCache.getInstance(getProject())
			.resolveWithCaching(this, $resolver, true, incomplete, containingFile);
	}
"""

fun LangData.referenceMixins(nickname: String, outDir: File) {
	val outPsiDir = dir(outDir, "psi", nickname)
	referenceTypes.nameBased.forEach { referenceType ->
		val prefix = "$languageName$referenceType"
		val referenceTypeClassName = "${prefix}GeneratedMixin"
		@Language("JAVA")
		val referenceTypeClassContent = """
package $basePackage.psi.$nickname;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import org.ice1000.tt.psi.GeneralReference;
import org.ice1000.tt.psi.NameIdentifierCompletionProcessor;
import org.ice1000.tt.psi.NameIdentifierResolveProcessor;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.tt.psi.ResolveKt.resolveWithToList;
import static org.ice1000.tt.psi.UtilsKt.invalidName;

public abstract class $referenceTypeClassName extends GeneralReference implements $prefix {
	public $referenceTypeClassName(@NotNull ASTNode node) {
		super(node);
	}

	private static @NotNull ResolveCache.PolyVariantResolver<$referenceTypeClassName> resolver = (self, b) ->
		resolveWithToList(new NameIdentifierResolveProcessor(self.getCanonicalText()), self)
			.toArray(ResolveResult.EMPTY_ARRAY);

	${rename(prefix, referenceType)}
	${variants("new NameIdentifierCompletionProcessor()")}
	${multiResolve("resolver")}
}"""
		outPsiDir.resolve("$referenceTypeClassName.java").writeText(referenceTypeClassContent)
	}

	referenceTypes.custom.forEach { referenceType ->
		val prefix = "$languageName${referenceType.name}"
		val referenceTypeClassName = "${prefix}GeneratedMixin"
		@Language("JAVA")
		val referenceTypeClassCode = """
package $basePackage.psi.$nickname;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import org.ice1000.tt.psi.GeneralReference;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.tt.psi.ResolveKt.resolveWithToList;
import static org.ice1000.tt.psi.UtilsKt.invalidName;

public abstract class $referenceTypeClassName extends GeneralReference implements $prefix {
	public $referenceTypeClassName(@NotNull ASTNode node) {
		super(node);
	}

	${rename(prefix, referenceType.name)}
	${variants(referenceType.completion)}
	${multiResolve(referenceType.resolver)}
}"""
		outPsiDir.resolve("$referenceTypeClassName.java").writeText(referenceTypeClassCode)
	}
}

fun LangData.parser(nickname: String, outDir: File) {
	val outPsiDir = dir(outDir, "psi", nickname)
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

	public static @NotNull FlexAdapter ${nickname}Lexer() {
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

	public @NotNull @Override Lexer createLexer(Project project) { return $elementTypeClassName.${nickname}Lexer(); }
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
