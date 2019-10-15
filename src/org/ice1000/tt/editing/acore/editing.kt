package org.ice1000.tt.editing.acore

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.acore.*

class ACoreAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is ACoreDeclaration -> declaration(element, holder)
			is ACoreVariable -> variable(element, holder)
		}
	}

	private fun variable(element: ACoreVariable, holder: AnnotationHolder) {
		val resolution = element.reference?.resolve()
		if (resolution == null) holder.createErrorAnnotation(element, TTBundle.message("tt.lint.unresolved")).apply {
			textAttributes = ACoreGeneratedHighlighter.UNRESOLVED
			highlightType = ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
		}
	}

	private fun pattern(pattern: ACorePattern, holder: AnnotationHolder) {
		when (pattern) {
			is ACoreAtomPattern -> holder.createInfoAnnotation(pattern, null)
				.textAttributes = ACoreGeneratedHighlighter.FUNCTION_NAME
			is ACorePairPattern -> pattern.patternList.forEach { pattern(it, holder) }
		}
	}

	private fun declaration(element: ACoreDeclaration, holder: AnnotationHolder) {
		element.pattern?.let { pattern(it, holder) }
	}
}
