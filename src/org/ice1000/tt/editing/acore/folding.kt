package org.ice1000.tt.editing.acore

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.ice1000.tt.ACoreFile
import org.ice1000.tt.editing.*
import org.ice1000.tt.psi.acore.ACoreDeclaration
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
		else -> "..."
	}

	override fun isCollapsedByDefault(node: ASTNode) = node.psi !is ACoreDeclaration

	override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
		if (root !is ACoreFile) return emptyArray()
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
) : ACoreVisitor() {
	private companion object {
		private val types = listOf(
			ACoreTypes.BACKSLASH,
			ACoreTypes.ARROW,
			ACoreTypes.MUL,
			ACoreTypes.PI,
			ACoreTypes.SIGMA)
	}

	override fun visitElement(o: PsiElement?) {
		if (o == null) return
		if (o.elementType in types) descriptors.add(FoldingDescriptor(o, o.textRange))
	}

	override fun visitDeclaration(o: ACoreDeclaration) {
		val startLine = document.getLineNumber(o.startOffset)
		val endLine = document.getLineNumber(o.endOffset)
		val body = o.expressionList.getOrNull(1)
		if (body != null &&
			document.getLineNumber(startLine) != document.getLineNumber(endLine)) {
			descriptors.add(FoldingDescriptor(o, body.textRange))
		}
	}
}
