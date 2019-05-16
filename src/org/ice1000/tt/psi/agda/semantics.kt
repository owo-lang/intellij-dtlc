package org.ice1000.tt.psi.agda

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.util.IncorrectOperationException
import org.ice1000.tt.psi.PatternCompletionProcessor
import org.ice1000.tt.psi.PatternResolveProcessor
import org.ice1000.tt.psi.agda.impl.AgdaExpImpl
import org.ice1000.tt.psi.resolveWith
import org.ice1000.tt.psi.treeWalkUp

abstract class AgdaNameExpMixin(node: ASTNode) : AgdaExpImpl(node), PsiPolyVariantReference, AgdaNameExp {
	override fun isSoft() = true
	override fun getRangeInElement() = TextRange(0, textLength)

	override fun getElement() = this
	override fun getReference() = this
	override fun getReferences() = arrayOf(reference)
	override fun isReferenceTo(reference: PsiElement) = reference == resolve()
	override fun getCanonicalText(): String = text
	override fun resolve(): PsiElement? = multiResolve(false).firstOrNull()?.run { element }

	override fun bindToElement(element: PsiElement): PsiElement = throw IncorrectOperationException("Unsupported")
	override fun getName() = canonicalText
	override fun handleElementRename(newName: String): PsiElement? =
		throw IncorrectOperationException("Does not support Agda rename")

	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun getVariants(): Array<LookupElement> {
		val variantsProcessor = PatternCompletionProcessor(lookupElement =
		{ LookupElementBuilder.create(it.text).withIcon(it.getIcon(0)) })
		treeWalkUp(variantsProcessor, this, containingFile)
		return variantsProcessor.candidateSet.toTypedArray()
	}

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<AgdaNameExpMixin> { ref, _ ->
			val name = ref.canonicalText
			resolveWith(PatternResolveProcessor(name), ref)
		}
	}
}
