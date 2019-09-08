package org.ice1000.tt.psi.mlang

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.tt.psi.*

abstract class MlangRefExprMixin(node: ASTNode) : GeneralReference(node), MlangRefExpr {
	override fun handleElementRename(newName: String): PsiElement = replace(
		MlangTokenType.createRefExpr(text, project) ?: invalidName(newName))

	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	// TODO: symbol kind?
	override fun getVariants() = resolveWith(PatternCompletionProcessor(lookupElement = {
		val decl = PsiTreeUtil.getParentOfType(it, MlangGeneralDeclaration::class.java)
		defaultLookup(it).withTailText(decl?.parameters?.bodyText(80).orEmpty(), true)
	}), this)

	companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<MlangRefExprMixin> { ref, _ ->
			resolveWith(PatternResolveProcessor(ref.canonicalText), ref)
		}
	}
}
