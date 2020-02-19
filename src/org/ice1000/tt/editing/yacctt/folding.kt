package org.ice1000.tt.editing.yacctt

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import org.ice1000.tt.FOLDING_PLACEHOLDER
import org.ice1000.tt.editing.collectFoldRegions
import org.ice1000.tt.psi.*
import org.ice1000.tt.psi.yacctt.*

class YaccTTFoldingBuilder : FoldingBuilderEx(), DumbAware {
	override fun getPlaceholderText(node: ASTNode) = when (node.elementType) {
		YaccTTTokenType.BLOCK_COMMENT -> "{---}"
		YaccTTTypes.SYSTEM -> "[$FOLDING_PLACEHOLDER]"
		else -> FOLDING_PLACEHOLDER
	}

	override fun isCollapsedByDefault(node: ASTNode) = false

	override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
		if (root !is YaccTTFileImpl) return emptyArray()
		return collectFoldRegions(root) { FoldingVisitor(it, document) }
	}
}

private class FoldingVisitor(
	private val descriptors: MutableList<FoldingDescriptor>,
	private val document: Document
) : YaccTTVisitor() {
	override fun visitData(o: YaccTTData) {
		val labels = o.labelList.takeIf { it.size > 1 } ?: return
		descriptors += FoldingDescriptor(o, TextRange(labels.first().startOffset, labels.last().endOffset))
	}

	override fun visitSystem(o: YaccTTSystem) {
		val startLine = document.getLineNumber(o.startOffset)
		val endLine = document.getLineNumber(o.endOffset)
		if (startLine != endLine) descriptors.add(FoldingDescriptor(o, o.textRange))
	}

	override fun visitComment(comment: PsiComment) {
		if (comment.elementType == YaccTTTokenType.BLOCK_COMMENT)
			descriptors += FoldingDescriptor(comment, comment.textRange)
	}

	override fun visitSplitBody(o: YaccTTSplitBody) = layout(o)
	override fun visitExpWhere(o: YaccTTExpWhere) = layout(o)
	override fun visitMutual(o: YaccTTMutual) = layout(o)
	override fun visitLetExp(o: YaccTTLetExp) {
		val layoutStart = o.childrenWithLeaves.firstOrNull { it.elementType == YaccTTTypes.LAYOUT_START } ?: return
		val layoutEnd = layoutStart.rightSiblings.firstOrNull {
			it.elementType == YaccTTTypes.KW_IN
		}?.prevSiblingIgnoring<PsiElement>(TokenType.WHITE_SPACE) ?: return
		descriptors += FoldingDescriptor(o, TextRange(layoutStart.startOffset, layoutEnd.endOffset))
	}

	private fun layout(o: PsiElement) {
		val layoutStart = o.childrenWithLeaves.firstOrNull { it.elementType == YaccTTTypes.LAYOUT_START } ?: return
		val layoutEnd = layoutStart.rightSiblings.firstOrNull { it.elementType == YaccTTTypes.LAYOUT_END } ?: o.lastChild
		?: return
		descriptors += FoldingDescriptor(o, TextRange(layoutStart.startOffset, layoutEnd.endOffset))
	}

	override fun visitModule(o: YaccTTModule) {
		val imports = o.importList.takeIf { it.size > 1 } ?: return
		val startOffset = imports.first().moduleUsage?.startOffset ?: return
		descriptors += FoldingDescriptor(o, TextRange(startOffset, imports.last().endOffset))
	}
}
