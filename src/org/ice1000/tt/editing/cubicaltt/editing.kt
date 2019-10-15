package org.ice1000.tt.editing.cubicaltt

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.cubicaltt.CubicalTTData
import org.ice1000.tt.psi.cubicaltt.CubicalTTDecl
import org.ice1000.tt.psi.cubicaltt.CubicalTTElementType.cubicalttLexer
import org.ice1000.tt.psi.cubicaltt.CubicalTTNameDecl
import org.ice1000.tt.psi.cubicaltt.CubicalTTTokenType

class CubicalTTFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(cubicalttLexer(), CubicalTTTokenType.IDENTIFIERS, CubicalTTTokenType.COMMENTS, TokenSet.EMPTY)
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
		holder.createInfoAnnotation(name, null).textAttributes = CubicalTTGeneratedHighlighter.FUNCTION_NAME
	}

	private fun decl(element: CubicalTTDecl, holder: AnnotationHolder) {
		val name = element.firstChild as? CubicalTTNameDecl ?: return
		holder.createInfoAnnotation(name, null).textAttributes = CubicalTTGeneratedHighlighter.FUNCTION_NAME
	}
}
