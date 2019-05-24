package org.ice1000.tt.editing.redprl

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.tt.FOLDING_PLACEHOLDER
import org.ice1000.tt.editing.collectFoldRegions
import org.ice1000.tt.psi.*
import org.ice1000.tt.psi.redprl.*

class RedPrlFoldingBuilder : FoldingBuilderEx(), DumbAware {
	override fun getPlaceholderText(node: ASTNode) = when (node.elementType) {
		RedPrlTokenType.BLOCK_COMMENT -> "/***/"
		else -> when (node.psi) {
			is RedPrlMlDecl -> "{$FOLDING_PLACEHOLDER}"
			is RedPrlTermAndTac -> "($FOLDING_PLACEHOLDER)"
			else -> FOLDING_PLACEHOLDER
		}
	}

	override fun isCollapsedByDefault(node: ASTNode) = false

	override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
		if (root !is RedPrlFileImpl) return emptyArray()
		return collectFoldRegions(root) { FoldingVisitor(it, document) }
	}
}

private class FoldingVisitor(
	private val descriptors: MutableList<FoldingDescriptor>,
	private val document: Document
) : RedPrlVisitor() {

	override fun visitComment(comment: PsiComment?) {
		if (comment?.elementType == RedPrlTokenType.BLOCK_COMMENT)
			descriptors += FoldingDescriptor(comment, comment.textRange)
	}

	override fun visitTermAndTac(o: RedPrlTermAndTac) {
		if (o.firstChild?.elementType == RedPrlTypes.LPAREN)
			descriptors += FoldingDescriptor(o, o.textRange)
	}

	override fun visitMlDecl(o: RedPrlMlDecl) {
		val lBrace = o.childrenWithLeaves.firstOrNull { it.elementType == RedPrlTypes.LBRACKET } ?: return
		val rBrace = lBrace.rightSiblings.firstOrNull { it.elementType == RedPrlTypes.RBRACKET } ?: return
		descriptors += FoldingDescriptor(o, TextRange(lBrace.startOffset, rBrace.endOffset))
	}
}
