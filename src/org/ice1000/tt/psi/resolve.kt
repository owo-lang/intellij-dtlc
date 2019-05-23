package org.ice1000.tt.psi

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import org.ice1000.tt.orTrue

abstract class ResolveProcessor<ResolveResult> : PsiScopeProcessor {
	abstract val candidateSet: ArrayList<ResolveResult>
	override fun handleEvent(event: PsiScopeProcessor.Event, o: Any?) = Unit
	override fun <T : Any?> getHint(hintKey: Key<T>): T? = null
	protected val PsiElement.hasNoError get() = !PsiTreeUtil.hasErrorElements(this)
}

/**
 * @return if a declaration <strong>not</strong> found.
 */
fun treeWalkUp(
	processor: PsiScopeProcessor,
	entrance: PsiElement,
	maxScope: PsiElement?,
	state: ResolveState = ResolveState.initial()): Boolean {
	if (!entrance.isValid) return false
	var prevParent = entrance
	var scope: PsiElement? = entrance

	while (scope != null) {
		ProgressIndicatorProvider.checkCanceled()
		if (!scope.processDeclarations(processor, state, prevParent, entrance)) return false
		if (scope == maxScope) break
		prevParent = scope
		scope = prevParent.context
	}
	return true
}

inline fun <reified T> resolveWith(processor: ResolveProcessor<T>, ref: PsiReference): Array<T> {
	val file = ref.element.containingFile ?: return emptyArray()
	treeWalkUp(processor, ref.element, file)
	return processor.candidateSet.toTypedArray()
}

abstract class GeneralDeclaration(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner {
	abstract val type: PsiElement?
	override fun getName(): String? = nameIdentifier?.text
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		nameIdentifier?.let { processor.execute(it, state) }.orTrue()
}

abstract class GeneralNameIdentifier(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner {
	override fun getName(): String? = text
	override fun getNameIdentifier() = this
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) = processor.execute(this, state)
}

abstract class GeneralReference(node: ASTNode) : ASTWrapperPsiElement(node), PsiPolyVariantReference {
	override fun isSoft() = true
	override fun getRangeInElement() = TextRange(0, textLength)
	override fun getName(): String? = text

	override fun getElement() = this
	override fun getReference() = this
	override fun getReferences() = arrayOf(reference)
	override fun isReferenceTo(reference: PsiElement) = reference == resolve()
	override fun getCanonicalText() = text
	override fun resolve(): PsiElement? = multiResolve(false).firstOrNull()?.run { element }
	override fun bindToElement(element: PsiElement): PsiElement = throw IncorrectOperationException("Unsupported")
}
