package org.ice1000.tt.editing.mlpolyr

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.ice1000.tt.psi.elementType
import org.ice1000.tt.psi.endOffset
import org.ice1000.tt.psi.mlpolyr.*
import org.ice1000.tt.psi.startOffset

class MLPolyRAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is MLPolyRCon -> constructor(element, holder)
			is MLPolyRMr -> constructor(element, holder)
			is MLPolyRDef -> def(element, holder)
			is MLPolyRFunction -> function(element, holder)
		}
	}

	private fun function(element: MLPolyRFunction, holder: AnnotationHolder) {
		holder.createInfoAnnotation(element.namePat, null)
			.textAttributes = MLPolyRHighlighter.FUNCTION_DECL
	}

	private fun def(element: MLPolyRDef, holder: AnnotationHolder) {
		if (element.firstChild?.elementType == MLPolyRTypes.KW_VAL) element.pat?.let {
			holder.createInfoAnnotation(it, null)
				.textAttributes = MLPolyRHighlighter.VARIABLE_DECL
		}
	}

	private fun constructor(element: PsiElement, holder: AnnotationHolder) {
		val fst = element.firstChild?.startOffset ?: return
		val snd = element.children.getOrNull(0)?.endOffset ?: return
		holder.createInfoAnnotation(TextRange(fst, snd), null)
			.textAttributes = MLPolyRHighlighter.CONSTRUCTOR
	}
}
