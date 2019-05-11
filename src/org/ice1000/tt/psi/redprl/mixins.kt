package org.ice1000.tt.psi.redprl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import icons.TTIcons
import org.ice1000.tt.psi.GeneralDeclaration

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
