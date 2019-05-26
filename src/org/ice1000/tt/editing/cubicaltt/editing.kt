package org.ice1000.tt.editing.cubicaltt

import com.intellij.lang.BracePair
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.cubicaltt.*

class CubicalTTBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(CubicalTTTypes.LPAREN, CubicalTTTypes.RPAREN, false),
			BracePair(CubicalTTTypes.LT, CubicalTTTypes.GT, false),
			BracePair(CubicalTTTypes.KW_LET, CubicalTTTypes.KW_IN, false),
			BracePair(CubicalTTTypes.LBRACK, CubicalTTTypes.RBRACK, false),
			BracePair(CubicalTTTypes.LAYOUT_START, CubicalTTTypes.LAYOUT_END, false))
	}

	override fun getPairs() = PAIRS
}

class CubicalTTFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(cubicalTTLexer(), CubicalTTTokenType.IDENTIFIERS, CubicalTTTokenType.COMMENTS, TokenSet.EMPTY)
}

class CubicalTTAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is CubicalTTDecl -> decl(element, holder)
			is CubicalTTData -> data(element, holder)
		}
	}

	private fun data(element: CubicalTTData, holder: AnnotationHolder) {
		val name = element.nameDecl ?: return
		holder.createInfoAnnotation(name, null).textAttributes = CubicalTTHighlighter.FUNCTION_NAME
	}

	private fun decl(element: CubicalTTDecl, holder: AnnotationHolder) {
		val name = element.firstChild as? CubicalTTNameDecl ?: return
		holder.createInfoAnnotation(name, null).textAttributes = CubicalTTHighlighter.FUNCTION_NAME
	}
}
