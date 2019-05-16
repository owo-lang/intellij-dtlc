package org.ice1000.tt.editing.agda

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.tt.psi.agda.*
import org.ice1000.tt.psi.childrenWithLeaves

class AgdaAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is AgdaSignature -> signature(element, holder)
			is AgdaVisibility, is AgdaRenaming -> rename(element, holder)
			is AgdaRenamePair -> renamingPair(element, holder)
		}
	}

	private fun renamingPair(element: AgdaRenamePair, holder: AnnotationHolder) {
		val to = element.childrenWithLeaves.firstOrNull { it.text == "to" } ?: return
		holder.createInfoAnnotation(to, null).textAttributes = AgdaHighlighter.KEYWORD
	}

	private fun rename(element: PsiElement, holder: AnnotationHolder) {
		holder.createInfoAnnotation(element.firstChild, null).textAttributes = AgdaHighlighter.KEYWORD
	}

	private fun signature(element: AgdaSignature, holder: AnnotationHolder) {
		if (element.parent !is AgdaLayout) return
		element.nameDeclList.forEach {
			holder.createInfoAnnotation(it, null)
				.textAttributes = AgdaHighlighter.FUNCTION_NAME
		}
	}
}
