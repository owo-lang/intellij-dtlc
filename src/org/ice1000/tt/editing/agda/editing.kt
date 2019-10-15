package org.ice1000.tt.editing.agda

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.tt.psi.agda.AgdaLayout
import org.ice1000.tt.psi.agda.AgdaRenamePair
import org.ice1000.tt.psi.agda.AgdaSignature
import org.ice1000.tt.psi.childrenWithLeaves

class AgdaAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is AgdaSignature -> signature(element, holder)
			is AgdaRenamePair -> renamingPair(element, holder)
		}
	}

	private fun renamingPair(element: AgdaRenamePair, holder: AnnotationHolder) {
		val to = element.childrenWithLeaves.firstOrNull { it.text == "to" } ?: return
		holder.createInfoAnnotation(to, null).textAttributes = AgdaGeneratedHighlighter.KEYWORD
	}

	private fun signature(element: AgdaSignature, holder: AnnotationHolder) {
		if (element.parent !is AgdaLayout) return
		element.nameDeclList.forEach {
			holder.createInfoAnnotation(it, null)
				.textAttributes = AgdaGeneratedHighlighter.FUNCTION_NAME
		}
	}
}
