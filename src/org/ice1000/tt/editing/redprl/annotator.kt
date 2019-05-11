package org.ice1000.tt.editing.redprl

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.redprl.RedPrlHole
import org.ice1000.tt.psi.redprl.RedPrlMetaVar

class RedPrlAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is RedPrlMetaVar -> metaVar(element, holder)
			is RedPrlHole -> hole(element, holder)
		}
	}

	private fun hole(element: RedPrlHole, holder: AnnotationHolder) {
		holder.createWeakWarningAnnotation(element, TTBundle.message("redprl.linter.unsolved-hole", element.text))
	}

	private fun metaVar(element: RedPrlMetaVar, holder: AnnotationHolder) {
		holder.createInfoAnnotation(element, null).textAttributes = RedPrlHighlighter.HASH
	}
}
