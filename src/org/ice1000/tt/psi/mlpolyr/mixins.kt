package org.ice1000.tt.psi.mlpolyr

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.util.IncorrectOperationException
import icons.TTIcons

abstract class MLPolyRDeclaration(node: ASTNode) : ASTWrapperPsiElement(node), PsiNameIdentifierOwner {
	override fun getIcon(flags: Int) = TTIcons.MLPOLYR
	override fun getName() = nameIdentifier?.text
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): PsiElement {
		val newPattern = MLPolyRTokenType.createPat(newName, project)
			?: throw IncorrectOperationException("Invalid name: $newName")
		nameIdentifier?.replace(newPattern)
		return this
	}
}

interface MLPolyRPatOwner : PsiElement {
	val pat: MLPolyRPat?
}

abstract class MLPolyRPatOwnerMixin(node: ASTNode) : MLPolyRDeclaration(node), MLPolyRPatOwner {
	override fun getNameIdentifier() = pat
}
