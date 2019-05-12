package org.ice1000.tt.psi

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
import javax.swing.Icon

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

class PatternCompletionProcessor(
	private val icon: (PsiElement) -> Icon?,
	private val accessible: (PsiElement) -> Boolean = { true },
	private val typeText: (PsiElement) -> String = {
		val declaration = PsiTreeUtil.getParentOfType(it, GeneralDeclaration::class.java)
		declaration?.type?.text ?: "Unknown"
	},
	private val tailText: (PsiElement) -> String = { "" }
) : ResolveProcessor<LookupElementBuilder>() {
	override val candidateSet = ArrayList<LookupElementBuilder>(10)
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean {
		if (element !is IPattern<*>) return true
		return element.visit { variable ->
			if (!accessible(variable)) return@visit true
			candidateSet += LookupElementBuilder
				.create(variable.text)
				.withIcon(icon(variable))
				.withTailText(tailText(variable), true)
				.withTypeText(typeText(variable), true)
			true
		}
	}
}
