package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language

fun LangUtilGenTask.editing(nickname: String) {
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
