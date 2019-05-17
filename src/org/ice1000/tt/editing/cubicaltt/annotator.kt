package org.ice1000.tt.editing.cubicaltt

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.ice1000.tt.psi.cubicaltt.CubicalTTData
import org.ice1000.tt.psi.cubicaltt.CubicalTTDecl
import org.ice1000.tt.psi.cubicaltt.CubicalTTNameDecl

class CubicalTTAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is CubicalTTDecl -> decl(element, holder)
			is CubicalTTData -> data(element, holder)
		}
	}

	private fun data(element: CubicalTTData, holder: AnnotationHolder) {
		val name = element.nameDecl ?: return
		holder.createInfoAnnotation(name, null).textAttributes = CubicalTTHighlighter.FUNCTION_NAME
	}

	private fun decl(element: CubicalTTDecl, holder: AnnotationHolder) {
		val name = element.firstChild as? CubicalTTNameDecl ?: return
		holder.createInfoAnnotation(name, null).textAttributes = CubicalTTHighlighter.FUNCTION_NAME
	}
}
