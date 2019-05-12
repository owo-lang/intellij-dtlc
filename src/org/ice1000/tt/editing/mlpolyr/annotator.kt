package org.ice1000.tt.editing.mlpolyr

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.endOffset
import org.ice1000.tt.psi.mlpolyr.*
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
			textAttributes = MLPolyRHighlighter.UNRESOLVED
		}
		MLPolyRSymbolKind.Function, MLPolyRSymbolKind.RcFunction -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.FUNCTION_CALL
		MLPolyRSymbolKind.Parameter -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.PARAMETER_CALL
		MLPolyRSymbolKind.Variable -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.VARIABLE_CALL
		MLPolyRSymbolKind.Pattern -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.PATTERN_CALL
		MLPolyRSymbolKind.Field -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.FIELD_CALL
		MLPolyRSymbolKind.Unknown -> Unit
	}

	private fun generalPat(element: MLPolyRGeneralPat, holder: AnnotationHolder) = when (element.kind) {
		MLPolyRSymbolKind.Function, MLPolyRSymbolKind.RcFunction -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.FUNCTION_DECL
		MLPolyRSymbolKind.Parameter -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.PARAMETER_DECL
		MLPolyRSymbolKind.Variable -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.VARIABLE_DECL
		MLPolyRSymbolKind.Pattern -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.PATTERN_DECL
		MLPolyRSymbolKind.Field -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.FIELD_DECL
		MLPolyRSymbolKind.Unknown -> Unit
	}

	private fun constructor(element: PsiElement, holder: AnnotationHolder) {
		val fst = element.firstChild?.startOffset ?: return
		val snd = element.children.getOrNull(0)?.endOffset ?: return
		holder.createInfoAnnotation(TextRange(fst, snd), null)
			.textAttributes = MLPolyRHighlighter.CONSTRUCTOR
	}
}
