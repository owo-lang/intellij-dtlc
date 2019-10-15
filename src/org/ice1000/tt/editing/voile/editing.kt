package org.ice1000.tt.editing.voile

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.tt.psi.voile.VoileImplementation
import org.ice1000.tt.psi.voile.VoileNameDeclMixin
import org.ice1000.tt.psi.voile.VoileSignature

class VoileAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is VoileNameDeclMixin -> nameDeclMixin(element, holder)
		}
	}

	private fun nameDeclMixin(element: VoileNameDeclMixin, holder: AnnotationHolder) {
		val parent = element.parent ?: return
		if (parent is VoileImplementation || parent is VoileSignature) {
			holder.createInfoAnnotation(element, null).textAttributes = VoileGeneratedHighlighter.FUNCTION_NAME
		}
	}
}
