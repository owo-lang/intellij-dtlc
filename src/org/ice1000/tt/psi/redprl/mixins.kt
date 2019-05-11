package org.ice1000.tt.psi.redprl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import icons.TTIcons
import org.ice1000.tt.psi.GeneralDeclaration
import org.ice1000.tt.psi.GeneralNameIdentifier

abstract class RedPrlDeclaration(node: ASTNode) : GeneralDeclaration(node) {
	override val type: PsiElement? get() = null
	override fun getIcon(flags: Int) = TTIcons.RED_PRL
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): PsiElement {
		val newPattern = RedPrlTokenType.createOpDecl(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName")
		nameIdentifier?.replace(newPattern)
		return this
	}
}

interface RedPrlOpOwner : PsiElement {
	val opDecl: RedPrlOpDecl?
}

abstract class RedPrlOpOwnerMixin(node: ASTNode) : RedPrlDeclaration(node), RedPrlOpOwner {
	override fun getNameIdentifier() = opDecl
}

abstract class MLPolyROpDeclMixin(node: ASTNode) : GeneralNameIdentifier(node), RedPrlOpDecl {
	override fun getIcon(flags: Int) = TTIcons.MLPOLYR
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String) =
		replace(RedPrlTokenType.createOpDecl(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName"))
}
