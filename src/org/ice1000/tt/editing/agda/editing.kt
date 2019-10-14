package org.ice1000.tt.editing.agda

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.lang.BracePair
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.project.DumbAware
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import icons.TTIcons
import org.ice1000.tt.editing.SimpleProvider
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.editing.makeKeywordsCompletion
import org.ice1000.tt.psi.agda.*
import org.ice1000.tt.psi.childrenWithLeaves

class AgdaBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(AgdaTypes.OPEN_BRACE, AgdaTypes.CLOSE_BRACE, false),
			BracePair(AgdaTypes.OPEN_IDIOM_BRACKET, AgdaTypes.CLOSE_IDIOM_BRACKET, false),
			BracePair(AgdaTypes.KW_LET, AgdaTypes.KW_IN, false),
			BracePair(AgdaTypes.OPEN_PAREN, AgdaTypes.CLOSE_PAREN, false))
	}

	override fun getPairs() = PAIRS
}

class AgdaAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is AgdaSignature -> signature(element, holder)
			is AgdaRenamePair -> renamingPair(element, holder)
		}
	}

	private fun renamingPair(element: AgdaRenamePair, holder: AnnotationHolder) {
		val to = element.childrenWithLeaves.firstOrNull { it.text == "to" } ?: return
		holder.createInfoAnnotation(to, null).textAttributes = AgdaHighlighter.KEYWORD
	}

	private fun signature(element: AgdaSignature, holder: AnnotationHolder) {
		if (element.parent !is AgdaLayout) return
		element.nameDeclList.forEach {
			holder.createInfoAnnotation(it, null)
				.textAttributes = AgdaHighlighter.FUNCTION_NAME
		}
	}
}

class AgdaCompletionContributor : CompletionContributor(), DumbAware {
	private val keywords = makeKeywordsCompletion(TTIcons.AGDA, listOf(
		"no-eta-equality", "eta-equality",
		"quoteContext", "constructor", "coinductive", "unquoteDecl", "unquoteDef",
		"postulate", "primitive", "inductive", "quoteGoal", "quoteTerm",
		"variable", "abstract", "instance", "rewrite", "private", "overlap",
		"unquote", "pattern", "import", "module", "codata", "record", "infixl",
		"infixr", "mutual", "forall", "tactic", "syntax", "where", "field",
		"infix", "macro", "quote", "with", "open", "data", "let", "in", "do", "hiding", "renaming", "using"
	))

	init {
		extend(CompletionType.BASIC, PlatformPatterns.psiElement(), SimpleProvider(keywords))
	}
}
