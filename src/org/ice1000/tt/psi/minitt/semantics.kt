package org.ice1000.tt.psi.minitt

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import icons.TTIcons
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.*
import org.ice1000.tt.psi.minitt.impl.MiniTTExpressionImpl
import org.ice1000.tt.psi.minitt.impl.MiniTTPatternImpl

abstract class MiniTTVariableMixin(node: ASTNode) : GeneralReference(node), MiniTTVariable {
	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = element.containingFile ?: return emptyArray()
		if (!element.isValid || element.project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(element.project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun handleElementRename(newName: String): PsiElement? =
		replace(MiniTTTokenType.createVariable(newName, project) ?: invalidName(newName))

	override fun getVariants() = resolveWith(PatternCompletionProcessor(lookupElement = {
		LookupElementBuilder.create(it.text).withIcon(TTIcons.MINI_TT)
	}), this)

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<MiniTTVariableMixin> { ref, _ ->
			val name = ref.canonicalText
			resolveWith(PatternResolveProcessor(name) {
				if ((it as? IPattern<*>)?.parent !is MiniTTTypedPatternGeneratedMixin) it.text == name
				else it.text == name && PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(it, GeneralDeclaration::class.java), ref, true)
			}, ref)
		}
	}
}

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