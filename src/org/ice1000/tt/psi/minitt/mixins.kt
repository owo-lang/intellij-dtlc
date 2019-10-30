package org.ice1000.tt.psi.minitt

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.util.IncorrectOperationException
import icons.TTIcons
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.GeneralNameIdentifier
import org.ice1000.tt.psi.childrenRevWithLeaves
import org.ice1000.tt.psi.invalidName
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

abstract class MiniTTGeneralPattern(node: ASTNode) : GeneralNameIdentifier(node) {
	override fun getIcon(flags: Int) = TTIcons.MINI_TT
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) =
		replace(MiniTTTokenType.createPattern(newName, project) ?: invalidName(newName))
}

abstract class MiniTTTypedAbstractionMixin(node: ASTNode) : MiniTTExpressionImpl(node), MiniTTTypedAbstraction {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		typedPattern.processDeclarations(processor, state, lastParent, place)
}

abstract class MiniTTDeclarationMixin(node: ASTNode) : MiniTTDeclarationGeneratedMixin(node), MiniTTDeclaration {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		childrenRevWithLeaves.filterIsInstance<MiniTTPrefixParameter>().all {
			it.typedPattern.processDeclarations(processor, state, lastParent, place)
		} && super.processDeclarations(processor, state, lastParent, place)
}
