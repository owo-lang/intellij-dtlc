package org.ice1000.tt.editing.mlpolyr

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.DumbAware
import com.intellij.patterns.PlatformPatterns.psiElement
import icons.TTIcons
import org.ice1000.tt.editing.SimpleProvider

class MLPolyRCompletionContributor : CompletionContributor(), DumbAware {
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
		"in"
	).map {
		LookupElementBuilder
			.create(it)
			.withTypeText("Keyword")
			.bold()
	}

	private val builtins = listOf(
		Triple("String.toInt", "string", "int"),
		Triple("String.fromInt", "int", "string"),
		Triple("String.inputLine", "()", "string"),
		Triple("String.size", "string", "int"),
		Triple("String.output", "string", "()"),
		Triple("String.sub", "(string, int)", "string"),
		Triple("String.concat", "string", "string list"),
		Triple("String.substring", "(string, int, int)", "string"),
		Triple("String.compare", "(string, string)", "int")
	).map { (it, tail, type) ->
		LookupElementBuilder
			.create(it)
			.withTypeText(type)
			.withTailText(" $tail")
			.withIcon(TTIcons.MLPOLYR)
	}

	private val all = keywords + builtins

	init {
		extend(CompletionType.BASIC, psiElement(), SimpleProvider(all))
	}
}
