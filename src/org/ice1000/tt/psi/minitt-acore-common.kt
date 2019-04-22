package org.ice1000.tt.psi

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import javax.swing.Icon

interface IMiniTTPattern<Var> : PsiElement {
	fun visit(visitor: (Var) -> Boolean): Boolean
}

interface TypedAbstractionOwner<Psi : PsiElement> : PsiElement {
	val typedAbstraction: Psi?
}

class SymbolResolveProcessor(
	@JvmField private val name: String,
	private val incompleteCode: Boolean
) : ResolveProcessor<PsiElementResolveResult>() {
	override val candidateSet = ArrayList<PsiElementResolveResult>(3)
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean = when {
		candidateSet.isNotEmpty() -> false
		element is IMiniTTPattern<*> -> {
			@Suppress("UNCHECKED_CAST")
			element as IMiniTTPattern<PsiElement>
			element.visit { variable ->
				val accessible = variable.text == name
				if (accessible) {
					val declaration = PsiTreeUtil.getParentOfType(variable, PsiNameIdentifierOwner::class.java)
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
	private val icon: Icon
) : ResolveProcessor<LookupElementBuilder>() {
	override val candidateSet = ArrayList<LookupElementBuilder>(10)
	override fun execute(element: PsiElement, resolveState: ResolveState): Boolean {
		if (element !is IMiniTTPattern<*>) return true
		@Suppress("UNCHECKED_CAST")
		element as IMiniTTPattern<PsiElement>
		return element.visit { variable ->
			val declaration = PsiTreeUtil.getParentOfType(variable, GeneralDeclaration::class.java)
			val type = declaration?.type?.text ?: "Unknown"
			candidateSet += LookupElementBuilder
				.create(variable.text)
				.withIcon(icon)
				.withTypeText(type, true)
			true
		}
	}
}
