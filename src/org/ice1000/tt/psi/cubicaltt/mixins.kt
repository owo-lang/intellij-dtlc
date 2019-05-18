package org.ice1000.tt.psi.cubicaltt

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.util.IncorrectOperationException
import icons.TTIcons
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.GeneralNameIdentifier
import org.ice1000.tt.psi.childrenRevWithLeaves
import org.ice1000.tt.psi.childrenWithLeaves

interface CubicalTTDecl : PsiElement, NavigationItem

abstract class CubicalTTModuleMixin : StubBasedPsiElementBase<CubicalTTModuleStub>, CubicalTTModule {
	constructor(node: ASTNode) : super(node)
	constructor(stub: CubicalTTModuleStub, type: IStubElementType<*, *>) : super(stub, type)
	constructor(stub: CubicalTTModuleStub, type: IElementType, node: ASTNode) : super(stub, type, node)

	override fun getNameIdentifier() = nameDecl
	override fun setName(newName: String): PsiElement {
		CubicalTTTokenType.createNameDecl(newName, project)?.let { nameDecl?.replace(it) }
		return this
	}

	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		childrenRevWithLeaves.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class CubicalTTDeclListMixin(node: ASTNode) : ASTWrapperPsiElement(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		childrenRevWithLeaves.filterNot { it is PsiWhiteSpace }.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class CubicalTTImportMixin(node: ASTNode) : ASTWrapperPsiElement(node), CubicalTTImport {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		moduleUsage?.reference?.resolve()?.processDeclarations(processor, state, lastParent, place).orTrue()
}

abstract class CubicalTTDeclMixin : StubBasedPsiElementBase<CubicalTTDeclStub>, CubicalTTDecl, PsiNameIdentifierOwner {
	constructor(node: ASTNode) : super(node)
	constructor(stub: CubicalTTDeclStub, type: IStubElementType<*, *>) : super(stub, type)
	constructor(stub: CubicalTTDeclStub, type: IElementType, node: ASTNode) : super(stub, type, node)

	override fun getNameIdentifier() = findChildByClass(CubicalTTNameDecl::class.java)
	override fun setName(newName: String): PsiElement {
		CubicalTTTokenType.createNameDecl(newName, project)?.let { nameIdentifier?.replace(it) }
		return this
	}

	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		childrenRevWithLeaves.filterIsInstance<CubicalTTTele>().all {
			it.processDeclarations(processor, state, lastParent, place)
		} && nameIdentifier?.processDeclarations(processor, state, lastParent, place).orTrue()
}

abstract class CubicalTTDefMixin : CubicalTTDeclMixin, CubicalTTDef, PsiNameIdentifierOwner {
	constructor(node: ASTNode) : super(node)
	constructor(stub: CubicalTTDeclStub, type: IStubElementType<*, *>) : super(stub, type)
	constructor(stub: CubicalTTDeclStub, type: IElementType, node: ASTNode) : super(stub, type, node)

	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		expWhere.childrenWithLeaves.all { it.processDeclarations(processor, state, lastParent, place) }
			&& super.processDeclarations(processor, state, lastParent, place).orTrue()
}

abstract class CubicalTTNameDeclMixin(node: ASTNode) : GeneralNameIdentifier(node), CubicalTTNameDecl {
	val kind: CubicalTTSymbolKind by lazy(::symbolKind)
	override fun getIcon(flags: Int) = kind.icon
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) = replace(CubicalTTTokenType.createNameDecl(newName, project)
		?: throw IncorrectOperationException("Invalid name: $newName"))
}
