package org.ice1000.tt.editing.redprl

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.tt.TTBundle
import org.ice1000.tt.psi.redprl.*

class RedPrlAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is RedPrlMetaDecl -> metaDecl(element, holder)
			is RedPrlMetaUsage -> metaCall(element, holder)
			is RedPrlOpDecl -> opDecl(element, holder)
			is RedPrlOpUsage -> opUsage(element, holder)
			is RedPrlVarDecl -> varDecl(element, holder)
			is RedPrlVarUsage -> varUsage(element, holder)
			is RedPrlHole -> hole(element, holder)
		}
	}

	private fun varUsage(element: RedPrlVarUsage, holder: AnnotationHolder) {
		holder.createInfoAnnotation(element, null).textAttributes = RedPrlGeneratedHighlighter.VAR_NAME_CALL
	}

	private fun varDecl(element: RedPrlVarDecl, holder: AnnotationHolder) {
		holder.createInfoAnnotation(element, null).textAttributes = RedPrlGeneratedHighlighter.VAR_NAME_DECL
	}

	private fun opUsage(element: RedPrlOpUsage, holder: AnnotationHolder) {
		holder.createInfoAnnotation(element, null).textAttributes = RedPrlGeneratedHighlighter.OP_NAME_CALL
	}

	private fun opDecl(element: RedPrlOpDecl, holder: AnnotationHolder) {
		holder.createInfoAnnotation(element, null).textAttributes = RedPrlGeneratedHighlighter.OP_NAME_DECL
	}

	private fun hole(element: RedPrlHole, holder: AnnotationHolder) {
		holder.createWeakWarningAnnotation(element, TTBundle.message("redprl.linter.unsolved-hole", element.text)).apply {
			textAttributes = RedPrlGeneratedHighlighter.HOLE
		}
	}

	private fun metaDecl(element: RedPrlMetaDecl, holder: AnnotationHolder) {
		holder.createInfoAnnotation(element, null).textAttributes = RedPrlGeneratedHighlighter.META_VAR_DECL
	}

	private fun metaCall(element: RedPrlMetaUsage, holder: AnnotationHolder) {
		holder.createInfoAnnotation(element, null).textAttributes = RedPrlGeneratedHighlighter.META_VAR_CALL
	}
}
