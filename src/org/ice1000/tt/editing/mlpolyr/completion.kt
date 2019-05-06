package org.ice1000.tt.editing.mlpolyr

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import icons.TTIcons
import org.ice1000.tt.editing.SimpleProvider

class MLPolyRCompletionContributor : CompletionContributor() {
	private val keywords = listOf(
		"rehandling",
		"handling",
		"default",
		"nocases",
		"orelse",
		"isnull",
		"false",
		"match",
		"cases",
		"where",
		"raise",
		"then",
		"else",
		"true",
		"with",
		"case",
		"let",
		"end",
		"fun",
		"and",
		"val",
		"try",
		"not",
		"if",
		"fn",
		"as",
		"of",
		"in").map {
		LookupElementBuilder
			.create(it)
			.withTypeText("Keyword")
			.withIcon(TTIcons.MLPOLYR)
			.bold()
	}

	init {
		extend(CompletionType.BASIC, psiElement(), SimpleProvider(keywords))
	}
}
