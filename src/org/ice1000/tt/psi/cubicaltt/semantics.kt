package org.ice1000.tt.psi.cubicaltt

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.util.IncorrectOperationException
import icons.SemanticIcons
import icons.TTIcons
import org.ice1000.tt.psi.*
import javax.swing.Icon

enum class CubicalTTSymbolKind(val icon: Icon?) {
	Parameter(SemanticIcons.ORANGE_P),
	Data(SemanticIcons.BLUE_HOLE),
	Function(SemanticIcons.PINK_LAMBDA),
}

fun CubicalTTNameDecl.symbolKind() = when (parent) {
	is CubicalTTTele -> CubicalTTSymbolKind.Parameter
	is CubicalTTHdata, is CubicalTTData -> CubicalTTSymbolKind.Data
	else -> CubicalTTSymbolKind.Function
}

abstract class CubicalTTModuleUsageMixin(node: ASTNode) : GeneralReference(node), CubicalTTModuleUsage {
	override fun handleElementRename(newName: String): PsiElement? = throw IncorrectOperationException("Invalid name: $newName")
	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || !file.isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, false, incompleteCode, file)
	}

	override fun getVariants() = modules(this)
		.mapNotNull { it.stub?.moduleName ?: it.nameDecl?.text }
		.map(LookupElementBuilder::create)
		.map { it.withIcon(TTIcons.CUBICAL_TT_FILE) }
		.toList()
		.toTypedArray()

	companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<CubicalTTModuleUsageMixin> { ref, _ ->
			modules(ref)
				.filter { (it.stub?.moduleName ?: it.nameDecl?.text) == ref.name }
				.map(::PsiElementResolveResult)
				.toList()
				.toTypedArray()
		}

		private fun modules(ref: CubicalTTModuleUsageMixin) = ref.containingFile
			?.containingDirectory
			?.files
			.orEmpty()
			.asSequence()
			.filterIsInstance<CubicalTTFileImpl>()
			.mapNotNull(CubicalTTFileImpl::module)
	}
}

abstract class CubicalTTNameMixin(node: ASTNode) : GeneralReference(node), CubicalTTNameExp, CubicalTTNameUsage {
	private val containingCubicalFile: CubicalTTFileImpl? get() = containingFile as? CubicalTTFileImpl
	override fun getCanonicalText(): String {
		val module = containingCubicalFile?.module ?: return text
		val moduleName = module.nameDecl?.text ?: return text
		return "$moduleName.$text"
	}

	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		if (!isValid || project.isDisposed) return emptyArray()
		val file = containingCubicalFile ?: return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun handleElementRename(newName: String): PsiElement? =
		replace(CubicalTTTokenType.createNameExp(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName"))

	override fun getVariants(): Array<LookupElement> {
		val variantsProcessor = NameIdentifierCompletionProcessor(lookupElement = {
			LookupElementBuilder
				.create(it.text)
				.withTypeText((it as? CubicalTTNameDeclMixin)?.kind?.name ?: "")
				.withIcon(it.getIcon(0) ?: TTIcons.CUBICAL_TT)
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