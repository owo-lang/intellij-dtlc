package org.ice1000.tt.editing.minitt

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.tt.psi.minitt.*

class MiniTTAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is MiniTTConstructor -> constructor(element, holder)
			is MiniTTDeclaration -> declaration(element, holder)
			is MiniTTConstDeclaration -> constDeclaration(element, holder)
			is MiniTTPatternMatch -> patternMatch(element, holder)
		}
	}

	private fun constDeclaration(element: MiniTTConstDeclaration, holder: AnnotationHolder) {
		element.pattern?.let { pattern(it, holder) }
	}

	private fun pattern(pattern: MiniTTPattern, holder: AnnotationHolder) {
		when (pattern) {
			is MiniTTAtomPattern -> holder.createInfoAnnotation(pattern, null)
				.textAttributes = MiniTTHighlighter.FUNCTION_NAME
			is MiniTTPairPattern -> pattern.patternList.forEach { pattern(it, holder) }
		}
	}

	private fun declaration(element: MiniTTDeclaration, holder: AnnotationHolder) {
		element.pattern?.let { pattern(it, holder) }
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
