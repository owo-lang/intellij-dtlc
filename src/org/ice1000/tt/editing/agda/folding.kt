package org.ice1000.tt.editing.agda

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.tt.AgdaFile
import org.ice1000.tt.FOLDING_PLACEHOLDER
import org.ice1000.tt.psi.*
import org.ice1000.tt.psi.agda.*

class AgdaFoldingBuilder : FoldingBuilderEx(), DumbAware {
	override fun getPlaceholderText(node: ASTNode) = when (node.elementType) {
		AgdaTypes.RECORD_EXP -> "{$FOLDING_PLACEHOLDER}"
		else -> FOLDING_PLACEHOLDER
	}

	override fun isCollapsedByDefault(node: ASTNode) = false

	override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
		if (root !is AgdaFile) return emptyArray()
		val descriptors = mutableListOf<FoldingDescriptor>()
		val visitor = FoldingVisitor(descriptors, document)
		PsiTreeUtil.processElements(root) {
			it.accept(visitor)
			true
		}
		return descriptors.toTypedArray()
	}
}

class FoldingVisitor(
	private val descriptors: MutableList<FoldingDescriptor>,
	private val document: Document
) : AgdaVisitor() {
	override fun visitLayout(o: AgdaLayout) = layout(o)
	override fun visitLayoutRecord(o: AgdaLayoutRecord) = layout(o)
	override fun visitLayoutSignature(o: AgdaLayoutSignature) = layout(o)

	override fun visitRecordExp(o: AgdaRecordExp) {
		val lBrace = o.childrenWithLeaves.firstOrNull { it.elementType == AgdaTypes.OPEN_BRACE } ?: return
		val rBrace = lBrace.rightSiblings.firstOrNull { it.elementType == AgdaTypes.CLOSE_BRACE } ?: return
		descriptors += FoldingDescriptor(o, TextRange(lBrace.startOffset, rBrace.endOffset))
	}

	private fun layout(o: PsiElement) {
		if (o.firstChild?.elementType == AgdaTypes.LAYOUT_START)
			descriptors += FoldingDescriptor(o, o.textRange)
	}
}
