package org.ice1000.tt.psi.minitt

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.PsiTreeUtil
import icons.TTIcons
import org.ice1000.tt.psi.*

abstract class MiniTTVariableMixin(node: ASTNode) : GeneralReference(node), MiniTTVariable {
	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = element.containingFile ?: return emptyArray()
		if (!element.isValid || element.project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(element.project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun handleElementRename(newName: String): PsiElement? =
		replace(MiniTTTokenType.createVariable(newName, project) ?: invalidName(newName))

	override fun getVariants() = resolveWith(PatternCompletionProcessor(lookupElement = {
		LookupElementBuilder.create(it.text).withIcon(TTIcons.MINI_TT)
	}), this)

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<MiniTTVariableMixin> { ref, _ ->
			val name = ref.canonicalText
			resolveWith(PatternResolveProcessor(name) {
				if ((it as? IPattern<*>)?.parent !is MiniTTTypedPatternMixin) it.text == name
				else it.text == name && PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(it, MiniTTGeneralDeclaration::class.java), ref, true)
			}, ref)
		}
	}
}