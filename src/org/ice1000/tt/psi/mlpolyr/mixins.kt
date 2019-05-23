package org.ice1000.tt.psi.mlpolyr

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import icons.TTIcons
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.GeneralDeclaration
import org.ice1000.tt.psi.GeneralNameIdentifier
import org.ice1000.tt.psi.IPattern
import org.ice1000.tt.psi.invalidName
import org.ice1000.tt.psi.mlpolyr.impl.MLPolyRExpImpl

abstract class MLPolyRDeclaration(node: ASTNode) : GeneralDeclaration(node) {
	override val type: PsiElement? get() = null
	override fun getIcon(flags: Int) = TTIcons.MLPOLYR
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): PsiElement {
		val newPattern = MLPolyRTokenType.createPat(newName, project) ?: invalidName(newName)
		nameIdentifier?.replace(newPattern)
		return this
	}
}

abstract class MLPolyRPatListOwnerMixin(node: ASTNode) : MLPolyRDeclaration(node), MLPolyRPatListOwner {
	override fun getNameIdentifier(): PsiElement? = null
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		patList.asReversed().all { it.processDeclarations(processor, state, lastParent, place) }
			&& super.processDeclarations(processor, state, lastParent, place)
}

abstract class MLPolyRFunctionMixin(node: ASTNode) : MLPolyRPatListOwnerMixin(node), MLPolyRFunction {
	override fun getNameIdentifier() = namePat
}

abstract class MLPolyRCbbPatMixin(node: ASTNode) : MLPolyRParameterPatMixin(node), MLPolyRCbbPat {
	override fun visit(visitor: (MLPolyRNamePat) -> Boolean) =
		fieldPatternList.all { PsiTreeUtil.findChildrenOfType(it, MLPolyRGeneralPat::class.java).all { it.visit(visitor) } }
}

abstract class MLPolyRNamePatMixin(node: ASTNode) : MLPolyRGeneralPat(node), MLPolyRNamePat {
	override fun visit(visitor: (MLPolyRNamePat) -> Boolean) = visitor(this)
}

abstract class MLPolyRParameterPatMixin(node: ASTNode) : MLPolyRGeneralPat(node), IPattern<MLPolyRNamePat> {
	override val kind get() = MLPolyRSymbolKind.Parameter
}

abstract class MLPolyRGeneralPat(node: ASTNode) : GeneralNameIdentifier(node), IPattern<MLPolyRNamePat> {
	open val kind: MLPolyRSymbolKind by lazy(::patSymbolKind)

	override fun getIcon(flags: Int) = kind.icon ?: TTIcons.MLPOLYR
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) =
		replace(MLPolyRTokenType.createPat(newName, project) ?: invalidName(newName))

	override fun visit(visitor: (MLPolyRNamePat) -> Boolean): Boolean =
		PsiTreeUtil.findChildrenOfType(this, MLPolyRGeneralPat::class.java).all { it.visit(visitor) }
}

interface MLPolyRPatOwner : PsiElement {
	val pat: MLPolyRPat?
}

interface MLPolyRPatListOwner : PsiElement {
	val patList: List<MLPolyRPat>
}

abstract class MLPolyRRcMixin(node: ASTNode) : MLPolyRDeclaration(node), MLPolyRRc {
	override fun getNameIdentifier() = namePat
}

abstract class MLPolyRPatOwnerMixin(node: ASTNode) : MLPolyRDeclaration(node), MLPolyRPatOwner {
	override fun getNameIdentifier() = pat
}

abstract class MLPolyRLetExpMixin(node: ASTNode) : MLPolyRExpImpl(node), MLPolyRLetExp {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		defList.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class MLPolyRDefMixin(node: ASTNode) : MLPolyRPatOwnerMixin(node), MLPolyRDef {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		functionList.all { it.processDeclarations(processor, state, lastParent, place) }
			&& optRcl?.rcList?.all { it.processDeclarations(processor, state, lastParent, place) }.orTrue()
			&& super.processDeclarations(processor, state, lastParent, place)
}

abstract class MLPolyRCasesExpMixin(node: ASTNode) : MLPolyRDeclaration(node), MLPolyRCasesExp {
	override fun getNameIdentifier(): PsiElement? = null
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		mrList.all { it.processDeclarations(processor, state, lastParent, place) }
}
