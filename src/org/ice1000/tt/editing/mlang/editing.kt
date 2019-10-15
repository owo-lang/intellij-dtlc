package org.ice1000.tt.editing.mlang

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import org.ice1000.tt.psi.mlang.MlangIdentMixin

class MlangAnnotator : Annotator, DumbAware {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		if (element is MlangIdentMixin) {
			holder.createInfoAnnotation(element, null).textAttributes = MlangGeneratedHighlighter.FUNCTION_NAME
		}
	}
}
