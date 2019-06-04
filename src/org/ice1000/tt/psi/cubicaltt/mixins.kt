package org.ice1000.tt.psi.cubicaltt

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
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
import org.ice1000.tt.psi.invalidName

interface CubicalTTDecl : PsiElement, NavigationItem

abstract class CubicalTTModuleMixin : StubBasedPsiElementBase<CubicalTTModuleStub>, CubicalTTModule {
	constructor(node: ASTNode) : super(node)
	constructor(stub: CubicalTTModuleStub, type: IStubElementType<*, *>) : super(stub, type)
	constructor(stub: CubicalTTModuleStub, type: IElementType, node: ASTNode) : super(stub, type, node)

	override fun getPresentation() = object : ItemPresentation {
		override fun getLocationString() = containingFile.name
		override fun getIcon(dark: Boolean) = TTIcons.CUBICAL_TT_FILE
		override fun getPresentableText() = name
	}

	override fun getIcon(flags: Int) = nameIdentifier?.getIcon(flags)
	override fun getNameIdentifier() = nameDecl
	override fun getName() = nameIdentifier?.text
	override fun toString() = "module $name"
	override fun setName(newName: String): PsiElement {
		CubicalTTTokenType.createNameDecl(newName, project)?.let { nameDecl?.replace(it) }
		return this
	}

	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		stub?.childrenStubs?.all { it.psi.processDeclarations(processor, state, lastParent, place) }
			?: childrenRevWithLeaves.filterNot { it is PsiWhiteSpace }.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class CubicalTTDeclListMixin(node: ASTNode) : ASTWrapperPsiElement(node) {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		childrenRevWithLeaves.filterNot { it is PsiWhiteSpace }.all { it.processDeclarations(processor, state, lastParent, place) }
}

abstract class CubicalTTImportMixin(node: ASTNode) : ASTWrapperPsiElement(node), CubicalTTImport {
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		moduleUsage?.reference?.resolve()?.processDeclarations(processor, state, lastParent, place).orTrue()
}

abstract class CubicalTTLabelMixin : StubBasedPsiElementBase<CubicalTTLabelStub>, CubicalTTLabel, PsiNameIdentifierOwner {
	constructor(node: ASTNode) : super(node)
	constructor(stub: CubicalTTLabelStub, type: IStubElementType<*, *>) : super(stub, type)
	constructor(stub: CubicalTTLabelStub, type: IElementType, node: ASTNode) : super(stub, type, node)

	override fun getNameIdentifier() = findChildByClass(CubicalTTNameDeclMixin::class.java)
	override fun toString() = "label $name"
	override fun getName() = nameIdentifier?.text
	override fun setName(newName: String): PsiElement {
		CubicalTTTokenType.createNameDecl(newName, project)?.let { nameIdentifier?.replace(it) }
		return this
	}

	override fun getPresentation() = object : ItemPresentation {
		override fun getLocationString() = containingFile.name
		override fun getIcon(dark: Boolean) = nameIdentifier?.getIcon(0) ?: TTIcons.CUBICAL_TT_FILE
		override fun getPresentableText() = name
	}

	override fun getIcon(flags: Int) = nameIdentifier?.getIcon(flags)
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		stub?.childrenStubs?.all {
			it.psi.processDeclarations(processor, state, lastParent, place)
		}?.let { it && processor.execute(this, state) }
			?: childrenRevWithLeaves.filterIsInstance<CubicalTTTele>().all {
			it.processDeclarations(processor, state, lastParent, place)
		} && nameIdentifier?.processDeclarations(processor, state, lastParent, place).orTrue()
}

abstract class CubicalTTDefMixin : StubBasedPsiElementBase<CubicalTTDefStub>, CubicalTTDef, PsiNameIdentifierOwner {
	constructor(node: ASTNode) : super(node)
	constructor(stub: CubicalTTDefStub, type: IStubElementType<*, *>) : super(stub, type)
	constructor(stub: CubicalTTDefStub, type: IElementType, node: ASTNode) : super(stub, type, node)

	override fun getNameIdentifier() = findChildByClass(CubicalTTNameDeclMixin::class.java)
	override fun toString() = "decl $name"
	override fun getName() = nameIdentifier?.text
	override fun setName(newName: String): PsiElement {
		CubicalTTTokenType.createNameDecl(newName, project)?.let { nameIdentifier?.replace(it) }
		return this
	}

	override fun getPresentation() = object : ItemPresentation {
		override fun getLocationString() = containingFile.name
		override fun getIcon(dark: Boolean) = nameIdentifier?.getIcon(0) ?: TTIcons.CUBICAL_TT_FILE
		override fun getPresentableText() = name
	}

	override fun getIcon(flags: Int) = nameIdentifier?.getIcon(flags)
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		stub?.childrenStubs?.all {
			it.psi.processDeclarations(processor, state, lastParent, place)
		}?.let { it && processor.execute(this, state) }
			?: childrenRevWithLeaves.filterIsInstance<CubicalTTTele>().all {
			it.processDeclarations(processor, state, lastParent, place)
		} && nameIdentifier?.processDeclarations(processor, state, lastParent, place).orTrue()
}

abstract class CubicalTTDataMixin : StubBasedPsiElementBase<CubicalTTDataStub>, CubicalTTData {
	constructor(node: ASTNode) : super(node)
	constructor(stub: CubicalTTDataStub, type: IStubElementType<*, *>) : super(stub, type)
	constructor(stub: CubicalTTDataStub, type: IElementType, node: ASTNode) : super(stub, type, node)

	override fun getNameIdentifier() = nameDecl
	override fun toString() = "data $name"
	override fun getName() = nameIdentifier?.text
	override fun getPresentation() = object : ItemPresentation {
		override fun getLocationString() = containingFile.name
		override fun getIcon(dark: Boolean) = nameIdentifier?.getIcon(0) ?: TTIcons.CUBICAL_TT_FILE
		override fun getPresentableText() = name
	}

	override fun setName(newName: String): PsiElement {
		CubicalTTTokenType.createNameDecl(newName, project)?.let { nameIdentifier?.replace(it) }
		return this
	}

	override fun getIcon(flags: Int) = nameIdentifier?.getIcon(flags)
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		stub?.childrenStubs?.all {
			it.psi.processDeclarations(processor, state, lastParent, place)
		}?.let { it && processor.execute(this, state) }
			?: labelList.all { it.processDeclarations(processor, state, lastParent, place) }
			&& nameIdentifier?.processDeclarations(processor, state, lastParent, place).orTrue()
}

abstract class CubicalTTNameDeclMixin(node: ASTNode) : GeneralNameIdentifier(node), CubicalTTNameDecl {
	val kind: CubicalTTSymbolKind by lazy(::symbolKind)
	override fun getIcon(flags: Int) = kind.icon
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) = replace(CubicalTTTokenType.createNameDecl(newName, project) ?: invalidName(newName))
}
