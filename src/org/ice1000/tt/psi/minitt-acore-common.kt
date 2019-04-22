package org.ice1000.tt.psi

import com.intellij.psi.PsiElement

interface IMiniTTPattern<Var> : PsiElement {
	fun visit(visitor: (Var) -> Boolean): Boolean
}
