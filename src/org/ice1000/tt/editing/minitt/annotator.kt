package org.ice1000.tt.editing.minitt

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.tt.psi.MiniTTBranches
import org.ice1000.tt.psi.MiniTTConstructor
import org.ice1000.tt.psi.MiniTTPatternMatch

class MiniTTAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is MiniTTConstructor -> constructor(element, holder)
			is MiniTTPatternMatch -> patternMatch(element, holder)
		}
	}

	private fun constructor(element: MiniTTConstructor, holder: AnnotationHolder) {
		if (element.parent is MiniTTBranches)
			holder.createInfoAnnotation(element.firstChild, null)
				.textAttributes = MiniTTHighlighter.CONSTRUCTOR_DECL
		else holder.createInfoAnnotation(element.firstChild, null)
			.textAttributes = MiniTTHighlighter.CONSTRUCTOR_CALL
	}

	private fun patternMatch(element: MiniTTPatternMatch, holder: AnnotationHolder) {
		holder.createInfoAnnotation(element.firstChild, null)
			.textAttributes = MiniTTHighlighter.CONSTRUCTOR_CALL
	}
}
