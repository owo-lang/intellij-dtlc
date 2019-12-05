package org.ice1000.tt.psi.narc

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import icons.TTIcons
import org.ice1000.tt.psi.*

abstract class NarcDataMixin(node: ASTNode) : NarcDataGeneratedMixin(node), NarcData {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		constructorList.all {
			it.processDeclarations(processor, state, lastParent, place)
		} && super.processDeclarations(processor, state, lastParent, place)
}

abstract class NarcCodataMixin(node: ASTNode) : NarcCodataGeneratedMixin(node), NarcCodata {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		projectionList.all {
			it.processDeclarations(processor, state, lastParent, place)
		} && super.processDeclarations(processor, state, lastParent, place)
}

abstract class NarcNameUsageMixin(node: ASTNode) : GeneralReference(node), NarcNameUsage {
	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun handleElementRename(newName: String): PsiElement? =
		replace(NarcTokenType.createNameUsage(newName, project) ?: invalidName(newName))

	override fun getVariants() = resolveWith(NameIdentifierCompletionProcessor(lookupElement = {
		LookupElementBuilder.create(it.text).withIcon(TTIcons.NARC)
	}), this)

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<NarcNameUsageMixin> { ref, _ ->
			resolveWith(NameIdentifierResolveProcessor(ref.canonicalText), ref)
		}
	}}
