package org.ice1000.tt.editing.yacctt

import com.intellij.lang.BracePair
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.yacctt.*
import org.ice1000.tt.psi.yacctt.YaccTTElementType.yaccTTLexer

class YaccTTBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(YaccTTTypes.LPAREN, YaccTTTypes.RPAREN, false),
			BracePair(YaccTTTypes.LT, YaccTTTypes.GT, false),
			BracePair(YaccTTTypes.KW_LET, YaccTTTypes.KW_IN, false),
			BracePair(YaccTTTypes.LBRACK, YaccTTTypes.RBRACK, false),
			BracePair(YaccTTTypes.LAYOUT_START, YaccTTTypes.LAYOUT_END, false))
	}

	override fun getPairs() = PAIRS
}

class YaccTTFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(yaccTTLexer(), YaccTTTokenType.IDENTIFIERS, YaccTTTokenType.COMMENTS, TokenSet.EMPTY)
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
