package org.ice1000.tt.editing.agda

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.openapi.project.DumbAware
import com.intellij.patterns.PlatformPatterns.psiElement
import icons.TTIcons
import org.ice1000.tt.editing.SimpleProvider
import org.ice1000.tt.editing.makeKeywordsCompletion

class AgdaCompletionContributor : CompletionContributor(), DumbAware {
	private val keywords = makeKeywordsCompletion(TTIcons.AGDA, listOf(
		"no-eta-equality", "eta-equality",
		"quoteContext", "constructor", "coinductive", "unquoteDecl", "unquoteDef",
		"postulate", "primitive", "inductive", "quoteGoal", "quoteTerm",
		"variable", "abstract", "instance", "rewrite", "private", "overlap",
		"unquote", "pattern", "import", "module", "codata", "record", "infixl",
		"infixr", "mutual", "forall", "tactic", "syntax", "where", "field",
		"infix", "macro", "quote", "with", "open", "data", "let", "in", "do"
	))

	init {
		extend(CompletionType.BASIC, psiElement(), SimpleProvider(keywords))
	}
}
