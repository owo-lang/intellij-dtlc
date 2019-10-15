package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language
import java.io.File

fun LangData.completionContributor(nickname: String, outDir: File) {
	val outEditingDir = outDir.resolve("editing").resolve(nickname)
	outEditingDir.mkdirs()

	val completionClassName = "${languageName}CompletionContributor"
	@Language("JAVA")
	val completionClassContent = """
package $basePackage.editing.$nickname;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.DumbAware;
import com.intellij.patterns.PlatformPatterns;
import icons.TTIcons;
import org.ice1000.tt.editing.SimpleProvider;

import java.util.List;

import static org.ice1000.tt.editing.CompletionKt.makeKeywordsCompletion;

public class $completionClassName extends CompletionContributor implements DumbAware {
	public $completionClassName() {
		List<LookupElementBuilder> keywords = makeKeywordsCompletion(TTIcons.${constantPrefix},
			${keywordList.joinToString { "\"$it\"" }}
		);
		extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new SimpleProvider(keywords));
	}
}"""
	outEditingDir.resolve("$completionClassName.java").writeText(completionClassContent)
}
