package org.ice1000.tt.psi.vitalyr

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import icons.SemanticIcons
import org.ice1000.tt.psi.GeneralNameIdentifier
import org.ice1000.tt.psi.invalidName

abstract class VitalyRNameDeclMixin(node: ASTNode) : GeneralNameIdentifier(node), VitalyRNameDecl {
	override fun getIcon(flags: Int) = SemanticIcons.BLUE_HOLE
	override fun setName(newName: String): PsiElement = replace(
		VitalyRTokenType.createNameDecl(newName, project) ?: invalidName(newName))
}
