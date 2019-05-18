package org.ice1000.tt.psi.cubicaltt

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.orTrue

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
		nameIdentifier?.processDeclarations(processor, state, lastParent, place).orTrue()
}

