package org.ice1000.tt.psi.redprl

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.util.IncorrectOperationException
import icons.TTIcons
import org.ice1000.tt.psi.*
import org.ice1000.tt.psi.redprl.impl.RedPrlMlValueImpl

interface RedPrlOpOwner : PsiElement {
	val opDecl: RedPrlOpDecl?
	/**
	 * Because I am supposed to let [RedPrlOpOwnerMixin]  extend [RedPrlMlCmd]
	 * but I cannot
	 */
	val mlCmd: RedPrlMlCmd?
}

abstract class RedPrlOpOwnerMixin(node: ASTNode) : GeneralDeclaration(node), RedPrlOpOwner {
	override fun getIcon(flags: Int) = TTIcons.RED_PRL
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): PsiElement {
		val newOpDecl = RedPrlTokenType.createOpDecl(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName")
		nameIdentifier?.replace(newOpDecl)
		return this
	}

	override fun getNameIdentifier() = opDecl
	val parameterText: String? get() = findChildByClass(RedPrlDeclArgumentsParens::class.java)?.bodyText(50)?.trim()
	override val type: PsiElement? get() = findChildByClass(RedPrlSort::class.java) ?: findChildByClass(RedPrlJudgment::class.java)
	override val mlCmd: RedPrlMlCmd? get() = findChildByClass(RedPrlMlCmd::class.java)
}

abstract class RedPrlOpDeclMixin(node: ASTNode) : GeneralNameIdentifier(node), RedPrlOpDecl {
	override fun visit(visitor: (RedPrlOpDecl) -> Boolean) = visitor(this)
	open val kind: RedPrlSymbolKind by lazy(::opSymbolKind)

	override fun getIcon(flags: Int) = kind.icon ?: TTIcons.RED_PRL
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) =
		replace(RedPrlTokenType.createOpDecl(newName, project)
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

	override fun getVariants(): Array<LookupElementBuilder> {
		val variantsProcessor = PatternCompletionProcessor(
			{ (it as? RedPrlOpDeclMixin)?.getIcon(0) },
			{ true },
			{
				(it.parent as? RedPrlOpOwnerMixin)?.type?.bodyText(60)
					?: (it as? RedPrlOpDeclMixin)?.kind?.name
					?: "??"
			},
			{ (it.parent as? RedPrlOpOwnerMixin)?.parameterText ?: "" })
		treeWalkUp(variantsProcessor, this, containingFile)
		return variantsProcessor.candidateSet.toTypedArray()
	}

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<RedPrlOpUsageMixin> { ref, _ ->
			resolveWith(PatternResolveProcessor(ref.canonicalText), ref)
		}
	}
}
