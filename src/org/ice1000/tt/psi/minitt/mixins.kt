package org.ice1000.tt.psi.minitt

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
import org.ice1000.tt.psi.minitt.impl.MiniTTExpressionImpl
import org.ice1000.tt.psi.minitt.impl.MiniTTPatternImpl

abstract class MiniTTAtomPatternMixin(node: ASTNode) : MiniTTPatternImpl(node), MiniTTAtomPattern {
	override fun visit(visitor: (MiniTTVariable) -> Boolean) =
		variable?.let(visitor).orTrue() && pattern?.visit(visitor).orTrue()
}

abstract class MiniTTPairPatternMixin(node: ASTNode) : MiniTTPatternImpl(node), MiniTTPairPattern {
	override fun visit(visitor: (MiniTTVariable) -> Boolean) = patternList.all { it.visit(visitor) }
}

abstract class MiniTTDeclarationExpressionMixin(node: ASTNode) : ASTWrapperPsiElement(node), MiniTTDeclarationExpression {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		declaration.processDeclarations(processor, state, lastParent, place)
}

abstract class MiniTTConstExpressionMixin(node: ASTNode) : ASTWrapperPsiElement(node), MiniTTConstExpression {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		constDeclaration.processDeclarations(processor, state, lastParent, place)
}

abstract class MiniTTGeneralDeclaration(node: ASTNode) : GeneralDeclaration(node) {
	override fun getIcon(flags: Int) = TTIcons.MINI_TT
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): PsiElement {
		val newPattern = MiniTTTokenType.createPattern(newName, project) ?: invalidName(newName)
		nameIdentifier?.replace(newPattern)
		return this
	}

	abstract override val type: MiniTTExpression?
}

abstract class MiniTTGeneralPattern(node: ASTNode) : GeneralNameIdentifier(node) {
	override fun getIcon(flags: Int) = TTIcons.MINI_TT
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) =
		replace(MiniTTTokenType.createPattern(newName, project) ?: invalidName(newName))
}

abstract class MiniTTLambdaExpressionMixin(node: ASTNode) : MiniTTGeneralDeclaration(node), MiniTTLambdaExpression {
	override fun getNameIdentifier() = pattern
	override val type: MiniTTExpression? get() = null
}

abstract class MiniTTTypedAbstractionMixin(node: ASTNode) : MiniTTExpressionImpl(node), MiniTTTypedAbstraction {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		typedPattern.processDeclarations(processor, state, lastParent, place).orTrue()
}

abstract class MiniTTDeclarationMixin(node: ASTNode) : MiniTTGeneralDeclaration(node), MiniTTDeclaration {
	override fun getNameIdentifier(): PsiElement? = pattern
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		childrenRevWithLeaves.filterIsInstance<MiniTTPrefixParameter>().all {
			it.typedPattern.processDeclarations(processor, state, lastParent, place)
		} && super.processDeclarations(processor, state, lastParent, place)

	override val type: MiniTTExpression?
		get() = childrenWithLeaves.filterIsInstance<MiniTTExpression>().firstOrNull {
			it.prevSiblingIgnoring<PsiElement>(TokenType.WHITE_SPACE)?.elementType == MiniTTTypes.COLON
		}
}

abstract class MiniTTConstDeclarationMixin(node: ASTNode) : MiniTTGeneralDeclaration(node), MiniTTConstDeclaration {
	override fun getNameIdentifier(): PsiElement? = pattern
	override val type: MiniTTExpression? get() = null
}

abstract class MiniTTTypedPatternMixin(node: ASTNode) : MiniTTGeneralDeclaration(node), MiniTTTypedPattern {
	override fun getNameIdentifier(): PsiElement? = pattern
	override val type: MiniTTExpression? get() = expression
}
