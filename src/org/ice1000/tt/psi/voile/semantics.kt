package org.ice1000.tt.psi.voile

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.ice1000.tt.psi.*

abstract class VoileNameUsageMixin(node: ASTNode) : GeneralReference(node), VoileNameUsage {
	override fun handleElementRename(newName: String): PsiElement = replace(
		VoileTokenType.createNameUsage(text, project) ?: invalidName(newName))

	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	// TODO: symbol kind?
	override fun getVariants() = resolveWith(NameIdentifierCompletionProcessor(), this)

	companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<VoileNameUsageMixin> { ref, _ ->
			resolveWith(NameIdentifierResolveProcessor(ref.canonicalText), ref)
		}
	}
}
