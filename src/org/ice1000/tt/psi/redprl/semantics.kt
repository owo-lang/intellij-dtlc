package org.ice1000.tt.psi.redprl

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import icons.SemanticIcons
import org.ice1000.tt.psi.*
import javax.swing.Icon

enum class RedPrlSymbolKind(val icon: Icon?) {
	Define(SemanticIcons.PINK_LAMBDA),
	Data(SemanticIcons.BLUE_HOLE),
	Value(SemanticIcons.ORANGE_V),
	Theorem(SemanticIcons.BLUE_T),
	Tactic(SemanticIcons.PURPLE_T),
	Parameter(SemanticIcons.ORANGE_P),
	MetaVar(SemanticIcons.ORANGE_P),
	Pattern(SemanticIcons.PURPLE_P),
	Unknown(null);
}

fun PsiElement.opSymbolKind() = when (val parent = parent) {
	is RedPrlMlDeclDef -> RedPrlSymbolKind.Define
	is RedPrlMlDeclData -> RedPrlSymbolKind.Data
	is RedPrlMlDeclTactic -> RedPrlSymbolKind.Tactic
	is RedPrlMlDeclTheorem -> RedPrlSymbolKind.Theorem
	is RedPrlDevMatchClauseMixin -> RedPrlSymbolKind.Pattern
	is RedPrlDeclArgument -> RedPrlSymbolKind.MetaVar
	is RedPrlHypBinding -> RedPrlSymbolKind.Parameter
	is RedPrlBoundVarsOwnerMixin -> RedPrlSymbolKind.Parameter
	is RedPrlBoundVarOwner -> {
		val aniki = parent.firstChild
		if (aniki?.elementType == RedPrlTypes.LAMBDA
			|| aniki?.nextSibling?.elementType == RedPrlTypes.LAMBDA)
			RedPrlSymbolKind.Parameter
		else RedPrlSymbolKind.Pattern
	}
	is RedPrlMlDeclVal -> RedPrlSymbolKind.Value
	else -> RedPrlSymbolKind.Unknown
}

private val PARAM_FAMILY = listOf(RedPrlSymbolKind.Parameter, RedPrlSymbolKind.Pattern, RedPrlSymbolKind.MetaVar)

private fun accessible(name: String, ref: PsiElement) = { it: PsiElement ->
	if ((it as? RedPrlKindedSymbol)?.kind !in PARAM_FAMILY) it.text == name
	else it.text == name && PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(it, RedPrlMlDecl::class.java), ref, false)
}

private fun completion(ref: PsiElement) = { it: PsiElement ->
	if ((it as? RedPrlKindedSymbol)?.kind !in PARAM_FAMILY) true
	else PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(it, RedPrlMlDecl::class.java), ref, false)
}

abstract class RedPrlMetaUsageMixin(node: ASTNode) : RedPrlVarUsageMixin(node), RedPrlMetaUsage/*, PsiPolyVariantReference*/ {
	override fun handleElementRename(newName: String): PsiElement? =
		replace(RedPrlTokenType.createMetaUsage(newName, project)
			?: invalidName(newName))

	override fun getVariants() = resolveWith(NameIdentifierCompletionProcessor(completion(this), {
		val declaration = PsiTreeUtil.getParentOfType(it, GeneralDeclaration::class.java)
		LookupElementBuilder
			.create(it.text)
			.withIcon(it.getIcon(0))
			.withTypeText(declaration?.type?.bodyText(40) ?: "Unknown", true)
	}), this)
}

abstract class RedPrlBoundVarMixin(node: ASTNode) : GeneralNameIdentifier(node), RedPrlBoundVar {
	override fun getIcon(flags: Int) = RedPrlSymbolKind.Pattern.icon
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) =
		replace(RedPrlTokenType.createBoundVar(newName, project) ?: invalidName(newName))
}

abstract class RedPrlOpUsageMixin(node: ASTNode) : GeneralReference(node), RedPrlOpUsage {
	override fun handleElementRename(newName: String): PsiElement? =
		replace(RedPrlTokenType.createOpUsage(newName, project) ?: invalidName(newName))

	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun getVariants() = resolveWith(NameIdentifierCompletionProcessor(completion(this), {
		LookupElementBuilder
			.create(it.text)
			.withIcon(it.getIcon(0))
			.withTypeText((it.parent as? GeneralDeclaration)?.type?.bodyText(40)
				?: (it as? RedPrlOpDeclMixin)?.kind?.name
				?: "??", true)
			.withTailText((it.parent as? RedPrlOpOwnerMixin)?.parameterText ?: "", true)
	}), this)

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<RedPrlOpUsageMixin> { ref, _ ->
			val name = ref.canonicalText
			resolveWith(NameIdentifierResolveProcessor(name, accessible(name, ref)), ref)
		}
	}
}

abstract class RedPrlVarUsageMixin(node: ASTNode) : GeneralReference(node), RedPrlVarUsage {
	override fun handleElementRename(newName: String): PsiElement? =
		replace(RedPrlTokenType.createVarUsage(newName, project) ?: invalidName(newName))

	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun getVariants() = resolveWith(NameIdentifierCompletionProcessor(completion(this), {
		LookupElementBuilder.create(it.text).withIcon(it.getIcon(0))
	}), this)

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<RedPrlVarUsageMixin> { ref, _ ->
			val name = ref.canonicalText
			resolveWith(NameIdentifierResolveProcessor(name, accessible(name, ref)), ref)
		}
	}
}
