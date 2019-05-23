package org.ice1000.tt.psi.agda

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.util.IncorrectOperationException
import org.ice1000.tt.psi.GeneralReference
import org.ice1000.tt.psi.NameIdentifierCompletionProcessor
import org.ice1000.tt.psi.NameIdentifierResolveProcessor
import org.ice1000.tt.psi.resolveWith

abstract class AgdaNameExpMixin(node: ASTNode) : GeneralReference(node), AgdaNameExp {
	override fun handleElementRename(newName: String): PsiElement? =
		throw IncorrectOperationException("Does not support Agda rename")

	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun getVariants() = resolveWith(NameIdentifierCompletionProcessor(lookupElement = {
		LookupElementBuilder.create(it.text).withIcon(it.getIcon(0))
	}), this)

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<AgdaNameExpMixin> { ref, _ ->
			val name = ref.canonicalText
			resolveWith(NameIdentifierResolveProcessor(name), ref)
		}
	}
}
