package org.ice1000.tt.editing.minitt

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import org.ice1000.tt.FOLDING_PLACEHOLDER
import org.ice1000.tt.MiniTTFile
import org.ice1000.tt.editing.*
import org.ice1000.tt.psi.elementType
import org.ice1000.tt.psi.endOffset
import org.ice1000.tt.psi.minitt.MiniTTDeclaration
import org.ice1000.tt.psi.minitt.MiniTTTypes
import org.ice1000.tt.psi.minitt.MiniTTVisitor
import org.ice1000.tt.psi.startOffset

class MiniTTFoldingBuilder : FoldingBuilderEx(), DumbAware {
	override fun getPlaceholderText(node: ASTNode) = when (node.elementType) {
		MiniTTTypes.LAMBDA -> LAMBDA
		MiniTTTypes.PI -> CAP_PI
		MiniTTTypes.MUL -> MULTIPLY
		MiniTTTypes.ARROW -> ARROW
		MiniTTTypes.DOUBLE_ARROW -> DOUBLE_ARROW
		MiniTTTypes.SIGMA -> CAP_SIGMA
		else -> FOLDING_PLACEHOLDER
	}

	override fun isCollapsedByDefault(node: ASTNode) = node.psi !is MiniTTDeclaration

	override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
		if (root !is MiniTTFile) return emptyArray()
		return collectFoldRegions(root) { FoldingVisitor(it, document) }
	}
}

private class FoldingVisitor(
	private val descriptors: MutableList<FoldingDescriptor>,
	private val document: Document
) : MiniTTVisitor() {
	private companion object {
		private val types = listOf(
			MiniTTTypes.MUL,
			MiniTTTypes.ARROW,
			MiniTTTypes.DOUBLE_ARROW,
			MiniTTTypes.LAMBDA,
			MiniTTTypes.PI,
			MiniTTTypes.SIGMA)
	}

	override fun visitElement(o: PsiElement) {
		if (o.elementType in types) descriptors.add(FoldingDescriptor(o, o.textRange))
	}

	override fun visitDeclaration(o: MiniTTDeclaration) {
		val startLine = document.getLineNumber(o.startOffset)
		val endLine = document.getLineNumber(o.endOffset)
		val body = o.expressionList.getOrNull(1)
		if (body != null && startLine != endLine) {
			descriptors.add(FoldingDescriptor(o, body.textRange))
		}
	}
}
