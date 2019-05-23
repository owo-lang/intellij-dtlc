package org.ice1000.tt.psi.redprl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.TokenType
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.util.IncorrectOperationException
import icons.TTIcons
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.*
import org.ice1000.tt.psi.redprl.impl.RedPrlTacImpl

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

interface RedPrlKindedSymbol {
	val kind: RedPrlSymbolKind
}

abstract class RedPrlBoundVarOwnerMixin(node: ASTNode) : GeneralDeclaration(node), RedPrlBoundVarOwner {
	override val type: PsiElement? get() = findChildByType<PsiElement>(RedPrlTypes.COLON)?.nextSiblingIgnoring(TokenType.WHITE_SPACE)
	override val boundVar: RedPrlBoundVar? get() = findChildByClass(RedPrlBoundVar::class.java)
	override fun getNameIdentifier(): PsiElement? = boundVar
	override fun setName(newName: String): PsiElement = throw IncorrectOperationException("Cannot rename!")
}

abstract class RedPrlWithTacMixin(node: ASTNode) : RedPrlTacImpl(node), RedPrlWithTac {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		hypBindingList.asReversed().all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class RedPrlDevDecompPatternOwnerMixin(node: ASTNode) : GeneralDeclaration(node) {
	override val type: PsiElement? get() = null
	override fun getNameIdentifier(): PsiElement? = findChildByClass(RedPrlDevDecompPattern::class.java)
	override fun setName(newName: String): PsiElement = throw IncorrectOperationException("Cannot rename!")
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) = childrenRevWithLeaves
		.filterIsInstance<RedPrlDevDecompPattern>()
		.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class RedPrlAnywayMixin(node: ASTNode) : ASTWrapperPsiElement(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		childrenRevWithLeaves.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class RedPrlBoundVarsOwnerMixin(node: ASTNode) : RedPrlBoundVarOwnerMixin(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) = childrenRevWithLeaves
		.filterIsInstance<RedPrlBoundVar>()
		.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class RedPrlDevMatchClauseMixin(node: ASTNode) : GeneralDeclaration(node), RedPrlDevMatchClause {
	override val type: PsiElement? get() = findChildByClass(RedPrlSort::class.java) ?: findChildByClass(RedPrlJudgment::class.java)
	override fun getIcon(flags: Int) = TTIcons.RED_PRL
	override fun setName(newName: String): PsiElement = throw IncorrectOperationException("Cannot rename!")
	override fun getNameIdentifier(): PsiElement? = findChildByClass(RedPrlVarDecl::class.java)
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) = childrenRevWithLeaves
		.filter { it is RedPrlVarDecl || it is RedPrlBoundVar || it is RedPrlDevDecompPatternOwnerMixin }
		.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class RedPrlVarOwnerMixin(node: ASTNode) : GeneralDeclaration(node), RedPrlVarOwner {
	override fun getIcon(flags: Int) = TTIcons.RED_PRL
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): PsiElement {
		val newOpDecl = varDecl?.run { RedPrlTokenType.createVarDecl(newName, project) } ?: invalidName(newName)
		nameIdentifier?.replace(newOpDecl)
		return this
	}

	override fun getNameIdentifier() = varDecl
	override val type: PsiElement? get() = findChildByClass(RedPrlJudgment::class.java)
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement): Boolean =
		super.processDeclarations(processor, state, lastParent, place) &&
			childrenRevWithLeaves.filterIsInstance<RedPrlVarOwnerMixin>().all {
				it.processDeclarations(processor, state, lastParent, place)
			}.orTrue()
}

abstract class RedPrlVarDeclMixin(node: ASTNode) : GeneralNameIdentifier(node), RedPrlVarDecl, RedPrlKindedSymbol {
	override val kind: RedPrlSymbolKind by lazy(::opSymbolKind)
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) =
		replace(RedPrlTokenType.createVarDecl(newName, project) ?: invalidName(newName))
}

abstract class RedPrlDeclArgumentMixin(node: ASTNode) : GeneralDeclaration(node), RedPrlDeclArgument {
	override fun getIcon(flags: Int) = TTIcons.RED_PRL
	override fun setName(newName: String): PsiElement = throw IncorrectOperationException("Cannot rename!")
	override fun getNameIdentifier() = metaDecl
	override val type: PsiElement? get() = findChildByType<PsiElement>(RedPrlTypes.COLON)?.nextSiblingIgnoring(TokenType.WHITE_SPACE)
}

abstract class RedPrlOpOwnerMixin(node: ASTNode) : GeneralDeclaration(node), RedPrlOpOwner {
	override fun getIcon(flags: Int) = TTIcons.RED_PRL
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): PsiElement {
		val newOpDecl = RedPrlTokenType.createOpDecl(newName, project) ?: invalidName(newName)
		nameIdentifier?.replace(newOpDecl)
		return this
	}

	override fun getNameIdentifier() = opDecl
	val parameterText: String? get() = parameter()?.bodyText(50)?.trim()
	override val type: PsiElement? get() = findChildByClass(RedPrlSort::class.java) ?: findChildByClass(RedPrlJudgment::class.java)
	override val mlCmd: RedPrlMlCmd? get() = findChildByClass(RedPrlMlCmd::class.java)
	private fun parameter() = findChildByClass(RedPrlDeclArgumentsParens::class.java)

	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		parameter()?.processDeclarations(processor, state, lastParent, place).orTrue()
			&& super.processDeclarations(processor, state, lastParent, place)
}

abstract class RedPrlDeclArgumentsParensMixin(node: ASTNode) : ASTWrapperPsiElement(node), RedPrlDeclArgumentsParens {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		declArgumentList.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class RedPrlOpDeclMixin(node: ASTNode) : GeneralNameIdentifier(node), RedPrlOpDecl, RedPrlKindedSymbol {
	override val kind: RedPrlSymbolKind by lazy(::opSymbolKind)
	override fun getIcon(flags: Int) = kind.icon ?: TTIcons.RED_PRL
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) =
		replace(RedPrlTokenType.createOpDecl(newName, project) ?: invalidName(newName))
}

abstract class RedPrlMetaDeclMixin(node: ASTNode) : GeneralNameIdentifier(node), RedPrlMetaDecl {
	override val kind: RedPrlSymbolKind by lazy(::opSymbolKind)
	override fun getIcon(flags: Int) = RedPrlSymbolKind.Parameter.icon
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) =
		replace(RedPrlTokenType.createMetaDecl(newName, project) ?: invalidName(newName))
}
