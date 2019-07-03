package org.ice1000.tt.psi.yacctt

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import icons.SemanticIcons
import icons.TTIcons
import org.ice1000.tt.psi.*
import javax.swing.Icon

enum class YaccTTSymbolKind(val icon: Icon?) {
	Parameter(SemanticIcons.ORANGE_P),
	Data(SemanticIcons.BLUE_HOLE),
	Function(SemanticIcons.PINK_LAMBDA),
}

fun YaccTTNameDecl.symbolKind() = when (parent) {
	is YaccTTTele -> YaccTTSymbolKind.Parameter
	is YaccTTData -> YaccTTSymbolKind.Data
	else -> YaccTTSymbolKind.Function
}

abstract class YaccTTModuleUsageMixin(node: ASTNode) : GeneralReference(node), YaccTTModuleUsage {
	override fun handleElementRename(newName: String): PsiElement? = invalidName(newName)
	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || !file.isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, false, incompleteCode, file)
	}

	override fun getVariants() = modules(this)
		.mapNotNull { it.stub?.moduleName ?: it.nameDecl?.text }
		.map(LookupElementBuilder::create)
		.map { it.withIcon(TTIcons.YACC_TT_FILE) }
		.toList()
		.toTypedArray()

	companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<YaccTTModuleUsageMixin> { ref, _ ->
			val name = ref.name.orEmpty()
			val stubBased = YaccTTModuleStubKey[name, ref.project, GlobalSearchScope.allScope(ref.project)]
			if (stubBased.isNotEmpty()) return@PolyVariantResolver stubBased.map(::PsiElementResolveResult).toTypedArray()
			modules(ref)
				.filter { (it.stub?.moduleName ?: it.nameDecl?.text) == name }
				.map(::PsiElementResolveResult)
				.toList()
				.toTypedArray()
		}

		private fun modules(ref: YaccTTModuleUsageMixin) = ref
			.containingFile
			?.containingDirectory
			?.files
			.orEmpty()
			.asSequence()
			.filterIsInstance<YaccTTFileImpl>()
			.mapNotNull(YaccTTFileImpl::module)
	}
}

abstract class YaccTTNameMixin(node: ASTNode) : GeneralReference(node), YaccTTNameExp, YaccTTNameUsage {
	private val containingYaccFile: YaccTTFileImpl? get() = containingFile as? YaccTTFileImpl
	override fun getCanonicalText(): String {
		val module = containingYaccFile?.module ?: return text
		val moduleName = module.nameDecl?.text ?: return text
		return "$moduleName.$text"
	}

	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		if (!isValid || project.isDisposed) return emptyArray()
		val file = containingYaccFile ?: return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun handleElementRename(newName: String): PsiElement? =
		replace(YaccTTTokenType.createNameExp(newName, project) ?: invalidName(newName))

	override fun getVariants() = resolveWith(NameIdentifierCompletionProcessor({
		if ((it as? YaccTTNameDeclMixin)?.kind !in paramFamily) true
		else PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(it, YaccTTDecl::class.java), this, false)
	}, {
		LookupElementBuilder
			.create(it.text)
			.withTypeText((it as? YaccTTNameDeclMixin)?.kind?.name ?: "")
			.withIcon(it.getIcon(0) ?: TTIcons.YACC_TT)
	}), this)

	private companion object ResolverHolder {
		val paramFamily = listOf(YaccTTSymbolKind.Parameter)

		private val resolver = ResolveCache.PolyVariantResolver<YaccTTNameMixin> { ref, _ ->
			val name = ref.name.orEmpty()
			var stubBased: Collection<PsiElement> = YaccTTDefStubKey[name, ref.project, GlobalSearchScope.fileScope(ref.containingFile)]
			if (stubBased.isEmpty()) stubBased = YaccTTLabelStubKey[name, ref.project, GlobalSearchScope.fileScope(ref.containingFile)]
			if (stubBased.isEmpty()) stubBased = YaccTTDataStubKey[name, ref.project, GlobalSearchScope.fileScope(ref.containingFile)]
			if (stubBased.isNotEmpty()) return@PolyVariantResolver stubBased.map(::PsiElementResolveResult).toTypedArray()
			resolveWith(NameIdentifierResolveProcessor(name) {
				if ((it as? YaccTTNameDeclMixin)?.kind !in paramFamily) it.text == name
				else it.text == name && PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(it, YaccTTDecl::class.java), ref, false)
			}, ref)
		}
	}
}