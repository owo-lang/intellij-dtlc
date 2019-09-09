package org.ice1000.tt.editing.mlang

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import org.ice1000.tt.FOLDING_PLACEHOLDER
import org.ice1000.tt.MlangFile
import org.ice1000.tt.editing.collectFoldRegions
import org.ice1000.tt.psi.*
import org.ice1000.tt.psi.mlang.*

class MlangFoldingBuilder : FoldingBuilderEx(), DumbAware {
	override fun getPlaceholderText(node: ASTNode) = when (node.elementType) {
		MlangTypes.LET_EXPR, MlangTypes.RECORD_EXPR, MlangTypes.SUM_EXPR, MlangTypes.PARAMETERS -> "{$FOLDING_PLACEHOLDER}"
		MlangTokenType.LINE_COMMENT -> "//..."
		MlangTokenType.BLOCK_COMMENT -> "/***/"
		else -> FOLDING_PLACEHOLDER
	}

	override fun isCollapsedByDefault(node: ASTNode) = false

	override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
		if (root !is MlangFile) return emptyArray()
		return collectFoldRegions(root) { FoldingVisitor(it, document) }
	}
}

private class FoldingVisitor(
	private val descriptors: MutableList<FoldingDescriptor>,
	private val document: Document
) : MlangVisitor() {

	override fun visitComment(comment: PsiComment?) {
		if (comment != null) descriptors += FoldingDescriptor(comment, comment.textRange)
	}

	override fun visitLetExpr(o: MlangLetExpr) = foldBetweenBraces(o)
	override fun visitRecordExpr(o: MlangRecordExpr) = foldBetweenBraces(o)
	override fun visitSumExpr(o: MlangSumExpr) = foldBetweenBraces(o)
	override fun visitParameters(o: MlangParameters) = foldBetweenBraces(o)

	private fun foldBetweenBraces(o: PsiElement) {
		val lBrace = o.childrenWithLeaves.firstOrNull { it.elementType == MlangTypes.LBRACE } ?: return
		val rBrace = lBrace.rightSiblings.firstOrNull { it.elementType == MlangTypes.RBRACE } ?: return
		descriptors += FoldingDescriptor(o, TextRange(lBrace.startOffset, rBrace.endOffset))
	}
}
