package org.ice1000.tt.psi.mlang

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import icons.SemanticIcons
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.*

/** Just a marker */
interface DeclarationMarker : PsiElement

abstract class MlangParamIdentMixin(node: ASTNode) : GeneralNameIdentifier(node), MlangParamIdent {
	override fun visit(visitor: (MlangParamIdentExplicit) -> Boolean) = paramIdentExplicit.visit(visitor)
	override fun setName(newName: String): PsiElement = replace(
		MlangTokenType.createParamIdent(newName, project) ?: invalidName(newName))
}

abstract class MlangLetMixin(node: ASTNode) : ASTWrapperPsiElement(node), MlangLetExpr {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		childrenRevWithLeaves.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class MlangCaseMixin(node: ASTNode) : GeneralNameIdentifier(node), MlangCase {
	override fun visit(visitor: (MlangParamIdentExplicit) -> Boolean) =
		paramIdentList.all { it.visit(visitor) } && tele?.visit(visitor).orTrue()
	override fun setName(newName: String): PsiElement = invalidName(newName)
}

abstract class MlangFieldMixin(node: ASTNode) : GeneralNameIdentifier(node), MlangField {
	override fun visit(visitor: (MlangParamIdentExplicit) -> Boolean) = param?.visit(visitor).orTrue()
	override fun setName(newName: String): PsiElement = invalidName(newName)
}

abstract class MlangPatternOwnerMixin(node: ASTNode) : GeneralNameIdentifier(node), IPattern<MlangParamIdentExplicit> {
	override fun visit(visitor: (MlangParamIdentExplicit) -> Boolean) =
		childrenWithLeaves.filterIsInstance<IPattern<MlangParamIdentExplicit>>().all { it.visit(visitor) }
	override fun setName(newName: String): PsiElement = invalidName(newName)
}

abstract class MlangLambdaMixin(node: ASTNode) : GeneralNameIdentifier(node), MlangLambdaExpr {
	override fun visit(visitor: (MlangParamIdentExplicit) -> Boolean) = paramIdent.visit(visitor)
	override fun setName(newName: String): PsiElement = invalidName(newName)
}

abstract class MlangParamMixin(node: ASTNode) : GeneralDeclaration(node), MlangParam {
	override fun getNameIdentifier(): PsiElement? = firstChild
	override fun visit(visitor: (MlangParamIdentExplicit) -> Boolean) = paramIdentList.all { it.visit(visitor) }
	override fun setName(newName: String): PsiElement = invalidName(newName)
	override val type: PsiElement? get() = term
}

abstract class MlangParametersMixin(node: ASTNode) : ASTWrapperPsiElement(node), MlangParameters  {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		tele?.processDeclarations(processor, state, lastParent, place).orTrue()
}

abstract class MlangManyParamsMixin(node: ASTNode) : GeneralNameIdentifier(node), IPattern<MlangParamIdentExplicit> {
	override fun visit(visitor: (MlangParamIdentExplicit) -> Boolean) = childrenWithLeaves.filterIsInstance<MlangParam>().all { it.visit(visitor) }
	override fun setName(newName: String): PsiElement = invalidName(newName)
}

abstract class MlangParamIdentExplicitMixin(node: ASTNode) : GeneralNameIdentifier(node), MlangParamIdentExplicit {
	override fun visit(visitor: (MlangParamIdentExplicit) -> Boolean) = visitor(this)
	override fun getIcon(flags: Int) = SemanticIcons.PURPLE_P
	override fun setName(newName: String): PsiElement = replace(
		MlangTokenType.createParamIdentExpl(newName, project) ?: invalidName(newName))
}

abstract class MlangIdentMixin(node: ASTNode) : GeneralNameIdentifier(node), MlangIdent {
	override fun visit(visitor: (MlangIdent) -> Boolean) = visitor(this)
	override fun getIcon(flags: Int) = SemanticIcons.BLUE_HOLE
	override fun setName(newName: String): PsiElement = replace(
		MlangTokenType.createIdent(newName, project) ?: invalidName(newName))
}

abstract class MlangGeneralDeclaration(node: ASTNode) : MlangGeneralDeclarationGeneratedMixin(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		parameters?.processDeclarations(processor, state, lastParent, place).orTrue() && super.processDeclarations(processor, state, lastParent, place)
	val parameters: MlangTele? get() = childrenWithLeaves.filterIsInstance<MlangTele>().firstOrNull()
}
