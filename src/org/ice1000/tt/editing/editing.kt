package org.ice1000.tt.editing

import com.intellij.lang.Commenter
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiNamedElement

abstract class TTFindUsagesProvider : FindUsagesProvider {
	override fun canFindUsagesFor(element: PsiElement) = element is PsiNameIdentifierOwner
	override fun getHelpId(psiElement: PsiElement): String? = null
	override fun getType(element: PsiElement) = ""
	override fun getDescriptiveName(element: PsiElement) = (element as? PsiNamedElement)?.name ?: ""
	override fun getNodeText(element: PsiElement, useFullName: Boolean) = getDescriptiveName(element)
}

class InplaceRenameRefactoringSupportProvider : RefactoringSupportProvider() {
	override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?) = true
}

abstract class TTCommenter : Commenter {
	override fun getCommentedBlockCommentPrefix() = blockCommentPrefix
	override fun getCommentedBlockCommentSuffix() = blockCommentSuffix
	override fun getBlockCommentPrefix(): String? = null
	override fun getBlockCommentSuffix(): String? = null
}
