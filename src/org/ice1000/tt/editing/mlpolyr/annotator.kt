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
			is MLPolyRGeneralPat -> generalPat(element, holder)
			is MLPolyRIdentifierMixin -> identifier(element, holder)
		}
	}

	private fun identifier(element: MLPolyRIdentifierMixin, holder: AnnotationHolder) = when ((element.resolve() as? MLPolyRGeneralPat)?.kind) {
		null -> {
			val daddy = element.parent
			if (daddy is MLPolyRMr || daddy is MLPolyRCon || daddy is MLPolyRLabel) Unit
			else holder.createInfoAnnotation(element, TTBundle.message("tt.lint.unresolved")).run {
				highlightType = ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
				textAttributes = MLPolyRHighlighter.UNRESOLVED
			}
		}
		SymbolKind.Function -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.FUNCTION_CALL
		SymbolKind.Parameter -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.PARAMETER_CALL
		SymbolKind.Variable -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.VARIABLE_CALL
		SymbolKind.Pattern -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.PATTERN_CALL
		SymbolKind.Unknown -> Unit
	}

	private fun generalPat(element: MLPolyRGeneralPat, holder: AnnotationHolder) = when (element.kind) {
		SymbolKind.Function -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.FUNCTION_DECL
		SymbolKind.Parameter -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.PARAMETER_DECL
		SymbolKind.Variable -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.VARIABLE_DECL
		SymbolKind.Pattern -> holder.createInfoAnnotation(element, null)
			.textAttributes = MLPolyRHighlighter.PATTERN_DECL
		SymbolKind.Unknown -> Unit
	}

	private fun constructor(element: PsiElement, holder: AnnotationHolder) {
		val fst = element.firstChild?.startOffset ?: return
		val snd = element.children.getOrNull(0)?.endOffset ?: return
		holder.createInfoAnnotation(TextRange(fst, snd), null)
			.textAttributes = MLPolyRHighlighter.CONSTRUCTOR
	}
}
