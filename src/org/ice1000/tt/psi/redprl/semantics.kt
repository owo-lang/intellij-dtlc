package org.ice1000.tt.psi.redprl

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import icons.SemanticIcons
import org.ice1000.tt.psi.*
import org.ice1000.tt.psi.redprl.impl.RedPrlMlValueImpl
import org.ice1000.tt.psi.redprl.impl.RedPrlTermAndTacImpl
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
			?: throw IncorrectOperationException("Invalid name: $newName"))

	override fun getVariants(): Array<LookupElement> {
		val variantsProcessor = NameIdentifierCompletionProcessor(completion(this), {
			val declaration = PsiTreeUtil.getParentOfType(it, GeneralDeclaration::class.java)
			LookupElementBuilder
				.create(it.text)
				.withIcon(it.getIcon(0))
				.withTypeText(declaration?.type?.bodyText(40) ?: "Unknown", true)
		})
		treeWalkUp(variantsProcessor, this, containingFile)
		return variantsProcessor.candidateSet.toTypedArray()
	}
}

abstract class RedPrlBoundVarMixin(node: ASTNode) : GeneralNameIdentifier(node), RedPrlBoundVar {
	override fun getIcon(flags: Int) = RedPrlSymbolKind.Pattern.icon
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) =
		replace(RedPrlTokenType.createBoundVar(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName"))
}

abstract class RedPrlOpUsageMixin(node: ASTNode) : RedPrlMlValueImpl(node), RedPrlOpUsage, PsiPolyVariantReference {
	override fun isSoft() = true
	override fun getRangeInElement() = TextRange(0, textLength)

	override fun getElement() = this
	override fun getReference() = this
	override fun getReferences() = arrayOf(reference)
	override fun isReferenceTo(reference: PsiElement) = reference == resolve()
	override fun getCanonicalText(): String = text
	override fun resolve(): PsiElement? = multiResolve(false).firstOrNull()?.run { element }

	override fun bindToElement(element: PsiElement): PsiElement = throw IncorrectOperationException("Unsupported")
	override fun handleElementRename(newName: String): PsiElement? =
		replace(RedPrlTokenType.createOpUsage(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName"))

	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun getVariants(): Array<LookupElement> {
		val variantsProcessor = NameIdentifierCompletionProcessor(completion(this), {
			LookupElementBuilder
				.create(it.text)
				.withIcon(it.getIcon(0))
				.withTypeText((it.parent as? GeneralDeclaration)?.type?.bodyText(40)
					?: (it as? RedPrlOpDeclMixin)?.kind?.name
					?: "??", true)
				.withTailText((it.parent as? RedPrlOpOwnerMixin)?.parameterText ?: "", true)
		})
		treeWalkUp(variantsProcessor, this, containingFile)
		return variantsProcessor.candidateSet.toTypedArray()
	}

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<RedPrlOpUsageMixin> { ref, _ ->
			val name = ref.canonicalText
			resolveWith(NameIdentifierResolveProcessor(name, accessible(name, ref)), ref)
		}
	}
}

abstract class RedPrlVarUsageMixin(node: ASTNode) : RedPrlTermAndTacImpl(node), RedPrlVarUsage, PsiPolyVariantReference {
	override fun isSoft() = true
	override fun getRangeInElement() = TextRange(0, textLength)

	override fun getElement() = this
	override fun getReference() = this
	override fun getReferences() = arrayOf(reference)
	override fun isReferenceTo(reference: PsiElement) = reference == resolve()
	override fun getCanonicalText(): String = text
	override fun resolve(): PsiElement? = multiResolve(false).firstOrNull()?.run { element }

	override fun bindToElement(element: PsiElement): PsiElement = throw IncorrectOperationException("Unsupported")
	override fun handleElementRename(newName: String): PsiElement? =
		replace(RedPrlTokenType.createVarUsage(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName"))

	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun getVariants(): Array<LookupElement> {
		val variantsProcessor = NameIdentifierCompletionProcessor(completion(this), {
			LookupElementBuilder.create(it.text).withIcon(it.getIcon(0))
		})
		treeWalkUp(variantsProcessor, this, containingFile)
		return variantsProcessor.candidateSet.toTypedArray()
	}

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<RedPrlVarUsageMixin> { ref, _ ->
			val name = ref.canonicalText
			resolveWith(NameIdentifierResolveProcessor(name, accessible(name, ref)), ref)
		}
	}
}
