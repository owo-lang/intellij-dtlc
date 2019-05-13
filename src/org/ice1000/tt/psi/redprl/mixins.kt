package org.ice1000.tt.psi.redprl

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import icons.TTIcons
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.*
import org.ice1000.tt.psi.redprl.impl.RedPrlMlValueImpl
import org.ice1000.tt.psi.redprl.impl.RedPrlTermAndTacImpl

interface RedPrlBoundVarOwner : PsiElement {
	val boundVar: RedPrlBoundVar?
}

interface RedPrlVarOwner : PsiElement {
	val varDecl: RedPrlVarDecl?
}

interface RedPrlOpOwner : PsiElement {
	val opDecl: RedPrlOpDecl?
	/**
	 * Because I am supposed to let [RedPrlOpOwnerMixin]  extend [RedPrlMlCmd]
	 * but I cannot
	 */
	val mlCmd: RedPrlMlCmd?
}

abstract class RedPrlBoundVarOwnerMixin(node: ASTNode) : GeneralDeclaration(node), RedPrlBoundVarOwner {
	override val type: PsiElement? get() = findChildByType<PsiElement>(RedPrlTypes.COLON)?.nextSibling
	override val boundVar: RedPrlBoundVar? get() = findChildByClass(RedPrlBoundVar::class.java)
	override fun getNameIdentifier(): PsiElement? = boundVar
	override fun setName(newName: String): PsiElement = throw IncorrectOperationException("Cannot rename!")
}

abstract class RedPrlDevDecompPatternOwnerMixin(node: ASTNode) : GeneralDeclaration(node) {
	override val type: PsiElement? get() = null
	override fun getNameIdentifier(): PsiElement? = findChildByClass(RedPrlDevDecompPattern::class.java)
	override fun setName(newName: String): PsiElement = throw IncorrectOperationException("Cannot rename!")
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) = PsiTreeUtil
		.getChildrenOfTypeAsList(this, RedPrlDevDecompPattern::class.java)
		.asReversed()
		.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class RedPrlBoundVarsOwnerMixin(node: ASTNode) : RedPrlBoundVarOwnerMixin(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) = PsiTreeUtil
		.getChildrenOfTypeAsList(this, RedPrlBoundVar::class.java)
		.asReversed()
		.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class RedPrlDevMatchClauseMixin(node: ASTNode) : GeneralDeclaration(node), RedPrlDevMatchClause {
	override val type: PsiElement? get() = findChildByClass(RedPrlSort::class.java) ?: findChildByClass(RedPrlJudgment::class.java)
	override fun getIcon(flags: Int) = TTIcons.RED_PRL
	override fun setName(newName: String): PsiElement = throw IncorrectOperationException("Cannot rename!")
	override fun getNameIdentifier(): PsiElement? = findChildByClass(RedPrlVarDecl::class.java)
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) = childrenWithLeaves
		.filter { it is RedPrlVarDecl || it is RedPrlBoundVar || it is RedPrlDevDecompPatternOwnerMixin }
		.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class RedPrlVarOwnerMixin(node: ASTNode) : GeneralDeclaration(node), RedPrlVarOwner {
	override fun getIcon(flags: Int) = TTIcons.RED_PRL
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): PsiElement {
		val newOpDecl = varDecl?.run { RedPrlTokenType.createVarDecl(newName, project) }
			?: throw IncorrectOperationException("Invalid name: $newName")
		nameIdentifier?.replace(newOpDecl)
		return this
	}

	override fun getNameIdentifier() = varDecl
	override val type: PsiElement? get() = findChildByClass(RedPrlJudgment::class.java)
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement): Boolean =
		super.processDeclarations(processor, state, lastParent, place) &&
			childrenWithLeaves.filterIsInstance<RedPrlVarOwnerMixin>().all {
				it.processDeclarations(processor, state, lastParent, place)
			}.orTrue()
}

abstract class RedPrlVarDeclMixin(node: ASTNode) : GeneralNameIdentifier(node), RedPrlVarDecl {
	override fun visit(visitor: (RedPrlVarDecl) -> Boolean) = visitor(this)
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) =
		replace(RedPrlTokenType.createVarDecl(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName"))
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
	val parameterText: String? get() = parameter()?.bodyText(50)?.trim()
	override val type: PsiElement? get() = findChildByClass(RedPrlSort::class.java) ?: findChildByClass(RedPrlJudgment::class.java)
	override val mlCmd: RedPrlMlCmd? get() = findChildByClass(RedPrlMlCmd::class.java)
	private fun parameter() = findChildByClass(RedPrlDeclArgumentsParens::class.java)

	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		parameter()?.run { declArgumentList.all { it.processDeclarations(processor, state, lastParent, place) } }.orTrue()
			&& super.processDeclarations(processor, state, lastParent, place)
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

abstract class RedPrlMetaDeclMixin(node: ASTNode) : ASTWrapperPsiElement(node), RedPrlMetaDecl {
	// TODO
}

abstract class RedPrlMetaUsageMixin(node: ASTNode) : ASTWrapperPsiElement(node), RedPrlMetaUsage {
	// TODO
}

abstract class RedPrlBoundVarMixin(node: ASTNode) : GeneralNameIdentifier(node), RedPrlBoundVar {
	override fun visit(visitor: (RedPrlBoundVar) -> Boolean) = visitor(this)
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

	override fun getVariants(): Array<LookupElementBuilder> {
		val variantsProcessor = PatternCompletionProcessor(
			{ it.getIcon(0) },
			{ true },
			{
				(it.parent as? GeneralDeclaration)?.type?.bodyText(40)
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

	override fun getVariants(): Array<LookupElementBuilder> {
		val variantsProcessor = PatternCompletionProcessor(
			{ it.getIcon(0) },
			{ true },
			{ (it.parent as? GeneralDeclaration)?.type?.bodyText(40) ?: "??" },
			{ (it.parent as? RedPrlOpOwnerMixin)?.parameterText ?: "" })
		treeWalkUp(variantsProcessor, this, containingFile)
		return variantsProcessor.candidateSet.toTypedArray()
	}

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<RedPrlVarUsageMixin> { ref, _ ->
			resolveWith(PatternResolveProcessor(ref.canonicalText), ref)
		}
	}
}
