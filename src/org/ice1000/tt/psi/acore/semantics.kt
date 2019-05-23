package org.ice1000.tt.psi.acore

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import icons.TTIcons
import org.ice1000.tt.psi.*

abstract class ACoreVariableMixin(node: ASTNode) : GeneralReference(node), ACoreVariable {
	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun handleElementRename(newName: String): PsiElement? =
		replace(ACoreTokenType.createVariable(newName, project) ?: invalidName(newName))

	override fun getVariants() = resolveWith(PatternCompletionProcessor(lookupElement = {
		LookupElementBuilder.create(it.text).withIcon(TTIcons.AGDA_CORE)
	}), this)

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<ACoreVariableMixin> { ref, _ ->
			resolveWith(PatternResolveProcessor(ref.canonicalText), ref)
		}
	}
}