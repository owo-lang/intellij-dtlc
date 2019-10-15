package org.ice1000.tt.editing.mlang

import com.intellij.lang.BracePair
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.psi.mlang.MlangIdentMixin
import org.ice1000.tt.psi.mlang.MlangTypes

class MlangBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(MlangTypes.LBRACE, MlangTypes.RBRACE, false),
			BracePair(MlangTypes.LBRACK, MlangTypes.RBRACK, false),
			BracePair(MlangTypes.LPAREN, MlangTypes.RPAREN, false))
	}

	override fun getPairs() = PAIRS
}

class MlangAnnotator : Annotator, DumbAware {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		if (element is MlangIdentMixin) {
			holder.createInfoAnnotation(element, null).textAttributes = MlangGeneratedHighlighter.FUNCTION_NAME
		}
	}
}
