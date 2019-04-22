package org.ice1000.tt.psi.acore

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.ice1000.tt.orTrue
import org.ice1000.tt.psi.acore.impl.ACorePatternImpl

interface IACorePattern : PsiElement {
	fun visit(visitor: (ACoreVariable) -> Boolean): Boolean
}

abstract class ACoreAtomPatternMixin(node: ASTNode) : ACorePatternImpl(node), ACoreAtomPattern {
	override fun visit(visitor: (ACoreVariable) -> Boolean) =
		variable?.let(visitor).orTrue() && pattern?.visit(visitor).orTrue()
}

abstract class ACorePairPatternMixin(node: ASTNode) : ACorePatternImpl(node), ACorePairPattern {
	override fun visit(visitor: (ACoreVariable) -> Boolean) = patternList.all { it.visit(visitor) }
}
