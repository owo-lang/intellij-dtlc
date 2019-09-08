package org.ice1000.tt.psi.mlang

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.util.IncorrectOperationException
import icons.SemanticIcons
import icons.TTIcons
import org.ice1000.tt.psi.*

abstract class MlangIdentMixin(node: ASTNode) : GeneralNameIdentifier(node), MlangIdent {
	override fun visit(visitor: (MlangIdent) -> Boolean) = visitor(this)
	override fun getIcon(flags: Int) = SemanticIcons.BLUE_HOLE
	override fun setName(newName: String): PsiElement = replace(
		MlangTokenType.createIdent(text, project) ?: invalidName(newName))
}

abstract class MlangGeneralDeclaration(node: ASTNode) : GeneralDeclaration(node) {
	override fun getNameIdentifier(): MlangIdent? = childrenWithLeaves.filterIsInstance<MlangIdent>().firstOrNull()
	override fun getIcon(flags: Int) = TTIcons.M_LANG
	@Throws(IncorrectOperationException::class)
	override fun setName(newName: String): PsiElement {
		val newIdentifier = MlangTokenType.createIdent(newName, project) ?: invalidName(newName)
		nameIdentifier?.replace(newIdentifier)
		return this
	}

	val parameters: MlangTele? get() = childrenWithLeaves.filterIsInstance<MlangTele>().firstOrNull()
	override val type: MlangTerm? get() = findChildByType<PsiElement>(MlangTypes.COLON)?.nextSiblingIgnoring(TokenType.WHITE_SPACE)
}
