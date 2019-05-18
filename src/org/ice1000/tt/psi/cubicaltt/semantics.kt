package org.ice1000.tt.psi.cubicaltt

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.util.IncorrectOperationException
import icons.TTIcons
import org.ice1000.tt.CubicalTTFile
import org.ice1000.tt.psi.*
import javax.swing.Icon

enum class CubicalTTSymbolKind(val icon: Icon?) {
}

abstract class CubicalTTNameMixin(node: ASTNode) : GeneralReference(node), CubicalTTNameExp, CubicalTTNameUsage {
	private val containingCubicalFile: CubicalTTFile? get() = containingFile as? CubicalTTFile
	override fun getCanonicalText(): String {
		val file = containingCubicalFile ?: return text
		val module = file
			.childrenWithLeaves
			.filterIsInstance<CubicalTTModule>()
			.firstOrNull() ?: return text
		val moduleName = module.nameDecl?.text ?: return text
		return "$moduleName.$text"
	}

	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingCubicalFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun handleElementRename(newName: String): PsiElement? =
		replace(CubicalTTTokenType.createNameExp(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName"))

	override fun getVariants(): Array<LookupElement> {
		val variantsProcessor = NameIdentifierCompletionProcessor(lookupElement = {
			LookupElementBuilder.create(it.text).withIcon(TTIcons.AGDA_CORE)
		})
		treeWalkUp(variantsProcessor, element, element.containingFile)
		return variantsProcessor.candidateSet.toTypedArray()
	}

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<CubicalTTNameMixin> { ref, _ ->
			resolveWith(NameIdentifierResolveProcessor(ref.text), ref)
		}
	}
}