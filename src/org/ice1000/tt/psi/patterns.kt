package org.ice1000.tt.psi

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.tt.orTrue

interface IPattern<Var : PsiElement> : PsiElement {
	fun visit(visitor: (Var) -> Boolean): Boolean
}

interface TypedAbstractionOwner<Psi : PsiElement> : PsiElement {
	val typedAbstraction: Psi?
}

abstract class TypedAbstractionOwnerMixin<Psi : PsiElement>(node: ASTNode)
	: ASTWrapperPsiElement(node), TypedAbstractionOwner<Psi> {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		typedAbstraction?.processDeclarations(processor, state, lastParent, place).orTrue()
}

class PatternResolveProcessor(
	private val name: String,
	private val accessible: (PsiElement) -> Boolean = { it.text == name }
) : ResolveProcessor<PsiElementResolveResult>() {
	override val candidateSet = ArrayList<PsiElementResolveResult>(3)
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean = when {
		candidateSet.isNotEmpty() -> false
		element is IPattern<*> -> {
			element.visit { variable ->
				val access = accessible(variable)
				if (access) {
					val declaration = PsiTreeUtil.getParentOfType(variable, PsiNameIdentifierOwner::class.java, false)
					if (declaration != null) candidateSet += PsiElementResolveResult(declaration, true)
				}
				!access
			}
		}
		else -> true
	}
}

class NameIdentifierResolveProcessor(
	private val name: String,
	private val accessible: (GeneralNameIdentifier) -> Boolean = { it.text == name }
) : ResolveProcessor<PsiElementResolveResult>() {
	override val candidateSet = ArrayList<PsiElementResolveResult>(3)
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean = when {
		candidateSet.isNotEmpty() -> false
		element is GeneralNameIdentifier -> {
			val access = accessible(element)
			if (access) candidateSet += PsiElementResolveResult(element, true)
			!access
		}
		else -> true
	}
}

class PatternCompletionProcessor(
	private val accessible: (PsiElement) -> Boolean = { true },
	private val lookupElement: (PsiElement) -> LookupElement = {
		val declaration = PsiTreeUtil.getParentOfType(it, GeneralDeclaration::class.java)
		LookupElementBuilder
			.create(it.text)
			.withIcon(it.getIcon(0))
			.withTypeText(declaration?.type?.bodyText(40) ?: "Unknown", true)
	}) : ResolveProcessor<LookupElement>() {
	override val candidateSet = ArrayList<LookupElement>(10)
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean {
		if (element !is IPattern<*>) return true
		return element.visit { variable ->
			if (!accessible(variable)) return@visit true
			candidateSet += lookupElement(variable)
			true
		}
	}
}

class NameIdentifierCompletionProcessor(
	private val accessible: (GeneralNameIdentifier) -> Boolean = { true },
	private val lookupElement: (GeneralNameIdentifier) -> LookupElement = {
		val declaration = PsiTreeUtil.getParentOfType(it, GeneralDeclaration::class.java)
		LookupElementBuilder
			.create(it.text)
			.withIcon(it.getIcon(0))
			.withTypeText(declaration?.type?.bodyText(40) ?: "Unknown", true)
	}) : ResolveProcessor<LookupElement>() {
	override val candidateSet = ArrayList<LookupElement>(10)
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean {
		if (element !is GeneralNameIdentifier) return true
		if (!accessible(element)) return true
		candidateSet += lookupElement(element)
		return true
	}
}
