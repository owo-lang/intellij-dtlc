package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language
import java.io.File

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
