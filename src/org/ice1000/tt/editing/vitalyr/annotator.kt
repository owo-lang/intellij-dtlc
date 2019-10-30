package org.ice1000.tt.editing.vitalyr

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtil
import org.ice1000.tt.TTBundle
import org.ice1000.tt.VITALYR_LANGUAGE_NAME
import org.ice1000.tt.psi.childrenWithLeaves
import org.ice1000.tt.psi.vitalyr.VitalyRExpr
import org.ice1000.tt.psi.vitalyr.VitalyRLambda
import org.ice1000.tt.psi.vitalyr.VitalyRTokenType

class VitalyRAnnotator : Annotator, DumbAware {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is VitalyRLambda -> lambda(element, holder)
		}
	}

	private fun lambda(element: VitalyRLambda, holder: AnnotationHolder) {
		if (PsiUtil.hasErrorElementChild(element)) return
		val expr = element.expr ?: return
		holder.createInfoAnnotation(expr, "null").registerFix(BrutalEval(expr))
	}
}

class BrutalEval(val expr: VitalyRExpr) : BaseIntentionAction(), DumbAware {
	override fun getFamilyName() = VITALYR_LANGUAGE_NAME
	override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?) = true
	override fun getText() = TTBundle.message("vitalyr.lint.brutal-normalize", expr.text)
	override operator fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
		if (PsiUtil.hasErrorElementChild(expr)) return
		ApplicationManager.getApplication().runWriteAction {
			val string = StringBuilder()
			val ctx = file?.childrenWithLeaves.orEmpty()
				.filterIsInstance<VitalyRLambda>()
				.filterNot(PsiUtil::hasErrorElementChild)
				.map { it.nameDecl!!.text to fromPsi(it.expr!!) }
				.toMap().toMutableMap()
			fromPsi(expr).bruteEval(ctx).toString(string, ToStrCtx.AbsBody)
			VitalyRTokenType.createExpr(string.toString(), project)?.let(expr::replace)
		}
	}
}