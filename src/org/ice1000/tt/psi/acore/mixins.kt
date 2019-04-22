package org.ice1000.tt.psi.acore

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.util.IncorrectOperationException
import icons.TTIcons
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.*
import org.ice1000.tt.psi.acore.impl.ACoreExpressionImpl
import org.ice1000.tt.psi.acore.impl.ACorePatternImpl

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

abstract class ACoreGeneralDeclaration(node: ASTNode) : GeneralDeclaration(node) {
	override fun getIcon(flags: Int) = TTIcons.AGDA_CORE
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): PsiElement {
		val newPattern = ACoreTokenType.createPattern(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName")
		nameIdentifier?.replace(newPattern)
		return this
	}

	abstract override val type: ACoreExpression?
}

abstract class ACoreLambdaMixin(node: ASTNode) : ACoreGeneralDeclaration(node), ACoreLambda {
	override fun getNameIdentifier() = pattern
	override val type: ACoreExpression? get() = null
}

abstract class ACoreTypedAbstractionMixin(node: ASTNode) : ACoreExpressionImpl(node), ACoreTypedAbstraction {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		typedPattern.processDeclarations(processor, state, lastParent, place).orTrue()
}

abstract class ACoreDeclarationMixin(node: ASTNode) : ACoreGeneralDeclaration(node), ACoreDeclaration {
	override fun getNameIdentifier(): PsiElement? = pattern
	override val type: ACoreExpression?
		get() = expressionList.firstOrNull {
			it.prevSiblingIgnoring<PsiElement>(TokenType.WHITE_SPACE)?.elementType == ACoreTypes.COLON
		}
}

abstract class ACoreTypedPatternMixin(node: ASTNode) : ACoreGeneralDeclaration(node), ACoreTypedPattern {
	override fun getNameIdentifier(): PsiElement? = pattern
	override val type: ACoreExpression? get() = expression
}

abstract class ACoreVariableMixin(node: ASTNode) : ACoreExpressionImpl(node), ACoreVariable, PsiPolyVariantReference {
	override fun isSoft() = true
	override fun getRangeInElement() = TextRange(0, textLength)

	override fun getReference() = this
	override fun getReferences() = arrayOf(reference)
	override fun isReferenceTo(reference: PsiElement) = reference == resolve()
	override fun getCanonicalText(): String = text
	override fun resolve(): PsiElement? = multiResolve(false).firstOrNull()?.run { element }
	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = element.containingFile ?: return emptyArray()
		if (!element.isValid || element.project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(element.project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun getElement() = this
	override fun bindToElement(element: PsiElement): PsiElement = throw IncorrectOperationException("Unsupported")
	override fun handleElementRename(newName: String): PsiElement? =
		replace(ACoreTokenType.createVariable(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName"))

	override fun getVariants(): Array<LookupElementBuilder> {
		val variantsProcessor = CompletionProcessor(true, TTIcons.AGDA_CORE)
		treeWalkUp(variantsProcessor, element, element.containingFile)
		return variantsProcessor.candidateSet.toTypedArray()
	}

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<ACoreVariableMixin> { ref, incompleteCode ->
			resolveWith(SymbolResolveProcessor(ref.canonicalText, incompleteCode), ref)
		}
	}
}
