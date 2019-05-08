package org.ice1000.tt.editing.mlpolyr

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.tt.FOLDING_PLACEHOLDER
import org.ice1000.tt.MLPolyRFile
import org.ice1000.tt.psi.childrenWithLeaves
import org.ice1000.tt.psi.elementType
import org.ice1000.tt.psi.mlpolyr.*
import org.ice1000.tt.psi.startOffset

class MLPolyRFoldingBuilder : FoldingBuilderEx(), DumbAware {
	override fun getPlaceholderText(node: ASTNode) = when (node.elementType) {
		MLPolyRTypes.RECORD_EXP -> "${node.firstChildNode?.text.orEmpty()}$FOLDING_PLACEHOLDER${node.lastChildNode?.text.orEmpty()}"
		MLPolyRTypes.LET_EXP -> "let $FOLDING_PLACEHOLDER"
		else -> FOLDING_PLACEHOLDER
	}

	override fun isCollapsedByDefault(node: ASTNode) = node.psi !is MLPolyRDeclaration

	override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
		if (root !is MLPolyRFile) return emptyArray()
		val descriptors = mutableListOf<FoldingDescriptor>()
		val visitor = FoldingVisitor(descriptors, document)
		PsiTreeUtil.processElements(root) {
			it.accept(visitor)
			true
		}
		return descriptors.toTypedArray()
	}

}

private class FoldingVisitor(
	private val descriptors: MutableList<FoldingDescriptor>,
	private val document: Document
) : MLPolyRVisitor() {

	override fun visitRecordExp(o: MLPolyRRecordExp) {
		descriptors += FoldingDescriptor(o, o.textRange)
	}

	override fun visitLetExp(o: MLPolyRLetExp) {
		val `in` = o.childrenWithLeaves.firstOrNull { it.elementType == MLPolyRTypes.KW_IN } ?: return
		val letLine = document.getLineNumber(o.startOffset)
		val inLine = document.getLineNumber(`in`.startOffset).coerceAtLeast(letLine + 1)
		descriptors += FoldingDescriptor(o, TextRange(o.firstChild.startOffset, document.getLineEndOffset(inLine-1)))
	}
}
