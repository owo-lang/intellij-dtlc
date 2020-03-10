package org.ice1000.tt.psi.mlang

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.tt.psi.*

fun mlangCompletion(mixin: PsiElement) = PatternCompletionProcessor(lookupElement = lookup@{
	if (it !is MlangIdentMixin) return@lookup defaultLookup(it)
	val decl = PsiTreeUtil.getParentOfType(it, MlangGeneralDeclaration::class.java)
	defaultLookup(it).withTailText(decl?.parameters?.bodyText(80).orEmpty(), true)
}, accessible = {
	if (it is MlangIdentMixin) true
	else PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(it, DeclarationMarker::class.java), mixin, false)
})

val MlangResolver = ResolveCache.PolyVariantResolver<MlangRefExprGeneratedMixin> { ref, _ ->
	val name = ref.name.orEmpty()
	resolveWith(PatternResolveProcessor(ref.canonicalText) {
		if (it is MlangIdentMixin) it.text == name
		else it.text == name && PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(it, DeclarationMarker::class.java), ref, false)
	}, ref)
}
