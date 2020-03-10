package org.ice1000.tt.psi.acore

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.util.IncorrectOperationException
import icons.TTIcons
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.*
import org.ice1000.tt.psi.acore.impl.ACoreExpressionImpl
import org.ice1000.tt.psi.acore.impl.ACorePatternImpl

abstract class ACoreVariableMixin(node: ASTNode) : GeneralReference(node), ACoreVariable {
	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun handleElementRename(newName: String): PsiElement? =
		replace(ACoreTokenType.createVariable(newName, project) ?: invalidName(newName))

	override fun getVariants() = resolveWith(PatternCompletionProcessor(lookupElement = {
		LookupElementBuilder.create(it.text).withIcon(TTIcons.AGDA_CORE)
	}), this)

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<ACoreVariableMixin> { ref, _ ->
			resolveWith(PatternResolveProcessor(ref.canonicalText), ref)
		}
	}
}

abstract class ACoreAtomPatternMixin(node: ASTNode) : ACorePatternImpl(node), ACoreAtomPattern {
	override fun visit(visitor: (ACoreVariable) -> Boolean) =
		variable?.let(visitor).orTrue() && pattern?.visit(visitor).orTrue()
}

abstract class ACorePairPatternMixin(node: ASTNode) : ACorePatternImpl(node), ACorePairPattern {
	override fun visit(visitor: (ACoreVariable) -> Boolean) = patternList.all { it.visit(visitor) }
}

abstract class ACoreDeclarationExpressionMixin(node: ASTNode) : ASTWrapperPsiElement(node), ACoreDeclarationExpression {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		declaration.processDeclarations(processor, state, lastParent, place)
}

abstract class ACoreGeneralPattern(node: ASTNode) : GeneralNameIdentifier(node) {
	override fun getIcon(flags: Int) = TTIcons.AGDA_CORE
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) =
		replace(ACoreTokenType.createPattern(newName, project)
			?: invalidName(newName))
}

abstract class ACoreTypedAbstractionMixin(node: ASTNode) : ACoreExpressionImpl(node), ACoreTypedAbstraction {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		typedPattern.processDeclarations(processor, state, lastParent, place)
}