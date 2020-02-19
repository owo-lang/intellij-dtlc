package org.ice1000.tt.editing.acore

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import org.ice1000.tt.ACoreFile
import org.ice1000.tt.FOLDING_PLACEHOLDER
import org.ice1000.tt.editing.*
import org.ice1000.tt.psi.acore.ACoreDeclaration
import org.ice1000.tt.psi.acore.ACoreEofVoid
import org.ice1000.tt.psi.acore.ACoreTypes
import org.ice1000.tt.psi.acore.ACoreVisitor
import org.ice1000.tt.psi.elementType
import org.ice1000.tt.psi.endOffset
import org.ice1000.tt.psi.startOffset

class ACoreFoldingBuilder : FoldingBuilderEx(), DumbAware {
	override fun getPlaceholderText(node: ASTNode) = when (node.elementType) {
		ACoreTypes.BACKSLASH -> LAMBDA
		ACoreTypes.PI -> CAP_PI
		ACoreTypes.SIGMA -> CAP_SIGMA
		ACoreTypes.MUL -> MULTIPLY
		ACoreTypes.ARROW -> ARROW
		ACoreTypes.VOID, ACoreTypes.EOF_VOID -> ""
		else -> FOLDING_PLACEHOLDER
	}

	override fun isCollapsedByDefault(node: ASTNode) = node.psi !is ACoreDeclaration

	override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
		if (root !is ACoreFile) return emptyArray()
		return collectFoldRegions(root) { FoldingVisitor(it, document) }
	}
}

private class FoldingVisitor(
	private val descriptors: MutableList<FoldingDescriptor>,
	private val document: Document
) : ACoreVisitor() {
	private companion object {
		private val types = listOf(
			ACoreTypes.BACKSLASH,
			ACoreTypes.ARROW,
			ACoreTypes.MUL,
			ACoreTypes.PI,
			ACoreTypes.SIGMA)
	}

	override fun visitElement(o: PsiElement) {
		if (o.elementType in types) descriptors.add(FoldingDescriptor(o, o.textRange))
	}

	override fun visitEofVoid(o: ACoreEofVoid) {
		descriptors.add(FoldingDescriptor(o, o.textRange))
	}

	override fun visitDeclaration(o: ACoreDeclaration) {
		val startLine = document.getLineNumber(o.startOffset)
		val endLine = document.getLineNumber(o.endOffset)
		val body = o.expressionList.getOrNull(1)
		if (body != null && startLine != endLine) {
			descriptors.add(FoldingDescriptor(o, body.textRange))
		}
	}
}
