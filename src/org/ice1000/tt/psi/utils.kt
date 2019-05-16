package org.ice1000.tt.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil

val PsiElement.elementType get() = node.elementType

val PsiElement.startOffset get() = textRange.startOffset
val PsiElement.endOffset get() = textRange.endOffset

inline fun <reified Psi : PsiElement> PsiElement.nextSiblingIgnoring(vararg types: IElementType): Psi? {
	var next: PsiElement = nextSibling ?: return null
	while (true) {
		next = next.nextSibling ?: return null
		return if (types.any { next.node.elementType == it }) continue
		else next as? Psi
	}
}

inline fun <reified Psi : PsiElement> PsiElement.prevSiblingIgnoring(vararg types: IElementType): Psi? {
	var next: PsiElement = prevSibling ?: return null
	while (true) {
		next = next.prevSibling ?: return null
		return if (types.any { next.node.elementType == it }) continue
		else next as? Psi
	}
}

val PsiElement.leftLeaves: Sequence<PsiElement>
	get() = generateSequence(this, PsiTreeUtil::prevLeaf).drop(1)

val PsiElement.rightSiblings: Sequence<PsiElement>
	get() = generateSequence(this.nextSibling) { it.nextSibling }

val PsiElement.leftSiblings: Sequence<PsiElement>
	get() = generateSequence(this.prevSibling) { it.prevSibling }

val PsiElement.childrenWithLeaves: Sequence<PsiElement>
	get() = generateSequence(this.firstChild) { it.nextSibling }

val PsiElement.childrenRevWithLeaves: Sequence<PsiElement>
	get() = generateSequence(this.lastChild) { it.prevSibling }

val PsiElement.ancestors: Sequence<PsiElement> get() = generateSequence(this) {
	if (it is PsiFile) null else it.parent
}

@JvmField val WHITE_SPACE = TokenSet.create(TokenType.WHITE_SPACE)

fun PsiElement.bodyText(maxSizeExpected: Int) = buildString {
	append(' ')
	var child = firstChild
	while (child != null && child != this@bodyText) {
		if (child is PsiWhiteSpace) append(' ')
		else {
			while (child.firstChild != null && length + child.textLength > maxSizeExpected) child = child.firstChild
			append(child.text)
		}
		if (maxSizeExpected < 0 || length >= maxSizeExpected) break
		do {
			child = child.nextSibling ?: child.parent
		} while (child == null)
	}
}
