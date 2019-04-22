package org.ice1000.tt.editing.acore

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.minitt.*

class ACoreAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is MiniTTDeclaration -> declaration(element, holder)
			is MiniTTVariable -> variable(element, holder)
		}
	}

	private fun variable(element: MiniTTVariable, holder: AnnotationHolder) {
		val resolution = element.reference?.resolve()
		if (resolution == null) holder.createErrorAnnotation(element, TTBundle.message("tt.lint.unresolved")).apply {
			textAttributes = ACoreHighlighter.UNRESOLVED
			highlightType = ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
		}
	}

	private fun pattern(pattern: MiniTTPattern, holder: AnnotationHolder) {
		when (pattern) {
			is MiniTTAtomPattern -> holder.createInfoAnnotation(pattern, null)
				.textAttributes = ACoreHighlighter.FUNCTION_NAME
			is MiniTTPairPattern -> pattern.patternList.forEach { pattern(it, holder) }
		}
	}

	private fun declaration(element: MiniTTDeclaration, holder: AnnotationHolder) {
		element.pattern?.let { pattern(it, holder) }
	}
}
