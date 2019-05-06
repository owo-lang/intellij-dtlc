package org.ice1000.tt.psi.mlpolyr

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.util.IncorrectOperationException
import icons.TTIcons
import org.ice1000.tt.psi.*
import org.ice1000.tt.psi.mlpolyr.impl.MLPolyRExpImpl

abstract class MLPolyRDeclaration(node: ASTNode) : GeneralDeclaration(node), PsiNameIdentifierOwner {
	override val type: PsiElement? get() = null
	override fun getIcon(flags: Int) = TTIcons.MLPOLYR
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): PsiElement {
		val newPattern = MLPolyRTokenType.createPat(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName")
		nameIdentifier?.replace(newPattern)
		return this
	}
}

interface MLPolyRPatOwner : PsiElement {
	val pat: MLPolyRPat?
}

abstract class MLPolyRPatOwnerMixin(node: ASTNode) : MLPolyRDeclaration(node), MLPolyRPatOwner {
	override fun getNameIdentifier() = pat
}

abstract class MLPolyRIdentifierMixin(node: ASTNode) : MLPolyRExpImpl(node), MLPolyRIdentifier, PsiPolyVariantReference {
	override fun isSoft() = true
	override fun getRangeInElement() = TextRange(0, textLength)

	override fun getReference() = this
	override fun getReferences() = arrayOf(reference)
	override fun isReferenceTo(reference: PsiElement) = reference == resolve()
	override fun getCanonicalText(): String = text
	override fun resolve(): PsiElement? = multiResolve(false).firstOrNull()?.run { element }

	override fun getElement() = this
	override fun bindToElement(element: PsiElement): PsiElement = throw IncorrectOperationException("Unsupported")
	override fun handleElementRename(newName: String): PsiElement? =
		replace(MLPolyRTokenType.createIdentifier(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName"))

	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = element.containingFile ?: return emptyArray()
		if (!element.isValid || element.project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(element.project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun getVariants(): Array<LookupElementBuilder> {
		val variantsProcessor = CompletionProcessor(true, TTIcons.MINI_TT)
		treeWalkUp(variantsProcessor, element, element.containingFile)
		return variantsProcessor.candidateSet.toTypedArray()
	}

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<MLPolyRIdentifierMixin> { ref, incompleteCode ->
			resolveWith(SymbolResolveProcessor(ref.canonicalText, incompleteCode), ref)
		}
	}
}
