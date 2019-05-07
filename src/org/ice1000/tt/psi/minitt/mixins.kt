package org.ice1000.tt.psi.minitt

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
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
		val newPattern = MiniTTTokenType.createPattern(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName")
		nameIdentifier?.replace(newPattern)
		return this
	}

	abstract override val type: MiniTTExpression?
}

abstract class MiniTTGeneralPattern(node: ASTNode) : GeneralNameIdentifier(node) {
	override fun getIcon(flags: Int) = TTIcons.MINI_TT
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) =
		replace(MiniTTTokenType.createPattern(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName"))
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
		prefixParameterList.asReversed().all { it.typedPattern.processDeclarations(processor, state, lastParent, place) }
			&& super.processDeclarations(processor, state, lastParent, place)

	override val type: MiniTTExpression?
		get() = expressionList.firstOrNull {
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

abstract class MiniTTVariableMixin(node: ASTNode) : ASTWrapperPsiElement(node), MiniTTVariable, PsiPolyVariantReference {
	override fun isSoft() = true
	override fun getRangeInElement() = TextRange(0, textLength)

	override fun getElement() = this
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

	override fun bindToElement(element: PsiElement): PsiElement = throw IncorrectOperationException("Unsupported")
	override fun handleElementRename(newName: String): PsiElement? =
		replace(MiniTTTokenType.createVariable(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName"))

	override fun getVariants(): Array<LookupElementBuilder> {
		val variantsProcessor = PatternCompletionProcessor(true, { TTIcons.MINI_TT })
		treeWalkUp(variantsProcessor, element, element.containingFile)
		return variantsProcessor.candidateSet.toTypedArray()
	}

	private companion object ResolverHolder {
		private val resolver = ResolveCache.PolyVariantResolver<MiniTTVariableMixin> { ref, incompleteCode ->
			val name = ref.canonicalText
			resolveWith(PatternResolveProcessor(name, incompleteCode) {
				if ((it as? IPattern<*>)?.parent !is MiniTTTypedPatternMixin) it.text == name
				else it.text == name && PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(it, MiniTTGeneralDeclaration::class.java), ref, true)
			}, ref)
		}
	}
}
