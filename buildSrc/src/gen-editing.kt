package org.ice1000.tt.gradle

import org.ice1000.tt.gradle.json.FindUsagesProviderOpt
import org.ice1000.tt.gradle.json.LangData
import org.intellij.lang.annotations.Language
import java.io.File

fun LangData.findUsages(nickname: String, outDir: File) {
	if (findUsagesProvider == FindUsagesProviderOpt.DontGenerate) return

	val className = "${languageName}FindUsagesProvider"
	@Language("JAVA")
	val classContent = """
package $basePackage.editing.$nickname;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.tree.TokenSet;
import $basePackage.editing.TTFindUsagesProvider;
import $basePackage.psi.$nickname.${languageName}TokenType;
import org.jetbrains.annotations.NotNull;

import static $basePackage.psi.$nickname.${languageName}ElementType.${nickname}Lexer;

public final class $className extends TTFindUsagesProvider {
	public @NotNull @Override WordsScanner getWordsScanner() {
		return new DefaultWordsScanner(
			${nickname}Lexer(),
			${languageName}TokenType.IDENTIFIERS,
			${languageName}TokenType.COMMENTS,
			${if (findUsagesProvider != FindUsagesProviderOpt.WithString) "TokenSet.EMPTY"
			else "${languageName}TokenType.STRINGS"});
	}
}"""
	outDir.resolve("editing")
		.resolve(nickname)
		.apply { mkdirs() }
		.resolve("$className.java").writeText(classContent)
}

fun LangData.braceMatcher(nickname: String, outDir: File) {
	val className = "${languageName}BraceMatcher"
	@Language("JAVA")
	val classContent = """
package $basePackage.editing.$nickname;

import com.intellij.lang.BracePair;
import $basePackage.editing.TTBraceMatcher;
import $basePackage.psi.$nickname.${languageName}Types;
import org.jetbrains.annotations.NotNull;

public final class $className extends TTBraceMatcher {
	private static BracePair[] PAIRS = new BracePair[]{
		${braceTokenPairs.entries.joinToString { (k, v) ->
		"new BracePair(${languageName}Types.$k, ${languageName}Types.$v, false)"
	}}
	};

	@Override public @NotNull BracePair[] getPairs() { return PAIRS; }
}
"""
	outDir.resolve("editing")
		.resolve(nickname)
		.apply { mkdirs() }
		.resolve("$className.java").writeText(classContent)
}

fun LangData.editing(outDir: File) {
	val className = "${languageName}DefaultContext"
	@Language("JAVA")
	val editing = """
package $basePackage.editing;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import $basePackage.*;
import org.jetbrains.annotations.NotNull;

import static $basePackage.ConstantsKt.${constantPrefix}_LANGUAGE_NAME;

public class $className extends TemplateContextType {
	public $className() {
		super("${constantPrefix}_DEFAULT_CONTEXT_ID", ${constantPrefix}_LANGUAGE_NAME, TTParentContext.class);
	}

	@Override
	public boolean isInContext(@NotNull PsiFile file, int offset) {
		return ${languageName}FileType.INSTANCE.equals(file.getFileType());
	}
}"""
	outDir.resolve("editing")
		.apply { mkdirs() }
		.resolve("$className.java").writeText(editing)
}
