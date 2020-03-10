package org.ice1000.tt.psi.mlang

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.tt.psi.*

abstract class MlangRefExprMixin(node: ASTNode) : GeneralReference(node), MlangRefExpr {
	override fun handleElementRename(newName: String): PsiElement = replace(
		MlangTokenType.createRefExpr(newName, project) ?: invalidName(newName))

	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, MlangResolver, true, incompleteCode, file)
	}

	override fun getVariants() = resolveWith(miniTTCompletion(this), this)
}

fun miniTTCompletion(mixin: MlangRefExprMixin) = PatternCompletionProcessor(lookupElement = lookup@{
	if (it !is MlangIdentMixin) return@lookup defaultLookup(it)
	val decl = PsiTreeUtil.getParentOfType(it, MlangGeneralDeclaration::class.java)
	defaultLookup(it).withTailText(decl?.parameters?.bodyText(80).orEmpty(), true)
}, accessible = {
	if (it is MlangIdentMixin) true
	else PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(it, DeclarationMarker::class.java), mixin, false)
})

private val MlangResolver = ResolveCache.PolyVariantResolver<MlangRefExprMixin> { ref, _ ->
	val name = ref.name.orEmpty()
	resolveWith(PatternResolveProcessor(ref.canonicalText) {
		if (it is MlangIdentMixin) it.text == name
		else it.text == name && PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(it, DeclarationMarker::class.java), ref, false)
	}, ref)
}
