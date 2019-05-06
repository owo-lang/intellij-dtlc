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

abstract class TypedAbstractionOwnerMixin<Psi: PsiElement>(node: ASTNode)
	: ASTWrapperPsiElement(node), TypedAbstractionOwner<Psi> {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		typedAbstraction?.processDeclarations(processor, state, lastParent, place).orTrue()
}

class SymbolResolveProcessor(
	@JvmField private val name: String,
	private val incompleteCode: Boolean
) : ResolveProcessor<PsiElementResolveResult>() {
	override val candidateSet = ArrayList<PsiElementResolveResult>(3)
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean = when {
		candidateSet.isNotEmpty() -> false
		element is IPattern<*> -> {
			element.visit { variable ->
				val accessible = variable.text == name
				if (accessible) {
					val declaration = PsiTreeUtil.getParentOfType(variable, PsiNameIdentifierOwner::class.java, false)
					if (declaration != null) candidateSet += PsiElementResolveResult(declaration, true)
				}
				!accessible
			}
		}
		else -> true
	}
}

class CompletionProcessor(
	private val incompleteCode: Boolean,
	private val icon: Icon,
	private val unknownType: String
) : ResolveProcessor<LookupElementBuilder>() {
	override val candidateSet = ArrayList<LookupElementBuilder>(10)
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean {
		if (element !is IPattern<*>) return true
		return element.visit { variable ->
			val declaration = PsiTreeUtil.getParentOfType(variable, GeneralDeclaration::class.java)
			val type = declaration?.type?.text ?: unknownType
			candidateSet += LookupElementBuilder
				.create(variable.text)
				.withIcon(icon)
				.withTypeText(type, true)
			true
		}
	}
}
