package org.ice1000.tt.editing

import com.intellij.lang.Commenter
import com.intellij.lang.PairedBraceMatcher
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil

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

abstract class TTBraceMatcher : PairedBraceMatcher {
	override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset
	override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true
}

abstract class TTCommenter : Commenter {
	override fun getCommentedBlockCommentPrefix() = blockCommentPrefix
	override fun getCommentedBlockCommentSuffix() = blockCommentSuffix
	override fun getBlockCommentPrefix(): String? = null
	override fun getBlockCommentSuffix(): String? = null
}

open class CxxLineCommenter : TTCommenter() {
	override fun getLineCommentPrefix() = "// "
}

class HaskellCommenter : TTCommenter() {
	override fun getBlockCommentPrefix() = "{-"
	override fun getBlockCommentSuffix() = "-}"
	override fun getLineCommentPrefix() = "--"
}

class CxxCommenter : CxxLineCommenter() {
	override fun getBlockCommentPrefix() = "/*"
	override fun getBlockCommentSuffix() = "*/"
}

inline fun collectFoldRegions(root: PsiElement, visitorFactory: (MutableList<FoldingDescriptor>) -> PsiElementVisitor): Array<FoldingDescriptor> {
	val descriptors = mutableListOf<FoldingDescriptor>()
	val visitor = visitorFactory(descriptors)
	PsiTreeUtil.processElements(root) {
		it.accept(visitor)
		true
	}
	return descriptors.toTypedArray()
}
