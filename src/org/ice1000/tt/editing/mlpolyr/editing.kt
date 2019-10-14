package org.ice1000.tt.editing.mlpolyr

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.BracePair
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import icons.TTIcons
import org.ice1000.tt.TTBundle
import org.ice1000.tt.editing.*
import org.ice1000.tt.psi.endOffset
import org.ice1000.tt.psi.mlpolyr.*
import org.ice1000.tt.psi.mlpolyr.MLPolyRElementType.mlPolyRLexer
import org.ice1000.tt.psi.startOffset

class MLPolyRAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is MLPolyRCon -> constructor(element, holder)
			is MLPolyRMr -> constructor(element, holder)
			// is MLPolyRCbbPat, is MLPolyRCbPat, is MLPolyRAsPat, is MLPolyRBqPat, is MLPolyRListPat -> Unit
			is MLPolyRGeneralPat -> generalPat(element, holder)
			is MLPolyRIdentifierMixin -> identifier(element, holder)
		}
	}

	private fun identifier(element: MLPolyRIdentifierMixin, holder: AnnotationHolder) = when ((element.resolve() as? MLPolyRGeneralPat)?.kind) {
		null -> holder.createInfoAnnotation(element, TTBundle.message("tt.lint.unresolved")).run {
			highlightType = ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
			textAttributes = MLPolyRGeneratedHighlighter.UNRESOLVED
		}
		MLPolyRSymbolKind.Function, MLPolyRSymbolKind.RcFunction -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRGeneratedHighlighter.FUNCTION_CALL
		MLPolyRSymbolKind.Parameter -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRGeneratedHighlighter.PARAMETER_CALL
		MLPolyRSymbolKind.Variable -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRGeneratedHighlighter.VARIABLE_CALL
		MLPolyRSymbolKind.Pattern -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRGeneratedHighlighter.PATTERN_CALL
		MLPolyRSymbolKind.Field -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRGeneratedHighlighter.FIELD_CALL
		MLPolyRSymbolKind.Unknown -> Unit
	}

	private fun generalPat(element: MLPolyRGeneralPat, holder: AnnotationHolder) = when (element.kind) {
		MLPolyRSymbolKind.Function, MLPolyRSymbolKind.RcFunction -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRGeneratedHighlighter.FUNCTION_DECL
		MLPolyRSymbolKind.Parameter -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRGeneratedHighlighter.PARAMETER_DECL
		MLPolyRSymbolKind.Variable -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRGeneratedHighlighter.VARIABLE_DECL
		MLPolyRSymbolKind.Pattern -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRGeneratedHighlighter.PATTERN_DECL
		MLPolyRSymbolKind.Field -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRGeneratedHighlighter.FIELD_DECL
		MLPolyRSymbolKind.Unknown -> Unit
	}

	private fun constructor(element: PsiElement, holder: AnnotationHolder) {
		val fst = element.firstChild?.startOffset ?: return
		val snd = element.children.getOrNull(0)?.endOffset ?: return
		holder.createInfoAnnotation(TextRange(fst, snd), null)
			.textAttributes = MLPolyRGeneratedHighlighter.CONSTRUCTOR
	}
}

class MLPolyRCommenter : TTCommenter() {
	override fun getBlockCommentPrefix() = "(*"
	override fun getBlockCommentSuffix() = "*)"
	override fun getLineCommentPrefix(): String? = null
}

class MLPolyRBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(MLPolyRTypes.LP, MLPolyRTypes.RP, false),
			BracePair(MLPolyRTypes.LSB, MLPolyRTypes.RSB, false),
			BracePair(MLPolyRTypes.LCBB, MLPolyRTypes.RCBB, false),
			BracePair(MLPolyRTypes.LCB, MLPolyRTypes.RCB, false))
	}

	override fun getPairs() = PAIRS
}

class MLPolyRFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(mlPolyRLexer(), MLPolyRTokenType.IDENTIFIERS, MLPolyRTokenType.COMMENTS, MLPolyRTokenType.STRINGS)
}

class MLPolyRCompletionContributor : CompletionContributor(), DumbAware {
	private val keywords = makeKeywordsCompletion(TTIcons.MLPOLYR, listOf(
		"rehandling",
		"handling", "default", "nocases", "orelse", "isnull", "false", "match",
		"cases", "where", "raise", "then", "else", "true", "with", "case", "let",
		"end", "fun", "and", "val", "try", "not", "if", "fn", "as", "of", "in"
	))

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
		extend(CompletionType.BASIC, PlatformPatterns.psiElement(), SimpleProvider(all))
	}
}
