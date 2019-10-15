package org.ice1000.tt.editing.yacctt

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.yacctt.YaccTTData
import org.ice1000.tt.psi.yacctt.YaccTTDecl
import org.ice1000.tt.psi.yacctt.YaccTTElementType.yaccttLexer
import org.ice1000.tt.psi.yacctt.YaccTTNameDecl
import org.ice1000.tt.psi.yacctt.YaccTTTokenType

class YaccTTFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(yaccttLexer(), YaccTTTokenType.IDENTIFIERS, YaccTTTokenType.COMMENTS, TokenSet.EMPTY)
}

class YaccTTAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is YaccTTDecl -> decl(element, holder)
			is YaccTTData -> data(element, holder)
		}
	}

	private fun data(element: YaccTTData, holder: AnnotationHolder) {
		val name = element.nameDecl ?: return
		holder.createInfoAnnotation(name, null).textAttributes = YaccTTGeneratedHighlighter.FUNCTION_NAME
	}

	private fun decl(element: YaccTTDecl, holder: AnnotationHolder) {
		val name = element.firstChild as? YaccTTNameDecl ?: return
		holder.createInfoAnnotation(name, null).textAttributes = YaccTTGeneratedHighlighter.FUNCTION_NAME
	}
}
