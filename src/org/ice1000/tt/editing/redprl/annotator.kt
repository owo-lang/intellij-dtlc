package org.ice1000.tt.editing.redprl

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.redprl.RedPrlHole
import org.ice1000.tt.psi.redprl.RedPrlMetaDecl
import org.ice1000.tt.psi.redprl.RedPrlMetaUsage

class RedPrlAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is RedPrlMetaDecl -> metaVar(element, holder)
			is RedPrlMetaUsage -> metaVar(element, holder)
			is RedPrlHole -> hole(element, holder)
		}
	}

	private fun hole(element: RedPrlHole, holder: AnnotationHolder) {
		holder.createWeakWarningAnnotation(element, TTBundle.message("redprl.linter.unsolved-hole", element.text)).apply {
			textAttributes = RedPrlHighlighter.HOLE
		}
	}

	private fun metaVar(element: PsiElement, holder: AnnotationHolder) {
		holder.createInfoAnnotation(element, null).textAttributes = RedPrlHighlighter.HASH
	}
}
