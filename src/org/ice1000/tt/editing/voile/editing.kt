package org.ice1000.tt.editing.voile

import com.intellij.lang.BracePair
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.editing.TTCommenter
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.redprl.redPrlLexer
import org.ice1000.tt.psi.voile.*

class VoileCommenter : TTCommenter() {
	override fun getLineCommentPrefix() = "// "
}

class VoileBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(VoileTypes.LPAREN, VoileTypes.RPAREN, false))
	}

	override fun getPairs() = PAIRS
}

class VoileFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(redPrlLexer(), VoileTokenType.IDENTIFIERS, VoileTokenType.COMMENTS, TokenSet.EMPTY)
}

class VoileAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is VoileNameDeclMixin -> nameDeclMixin(element, holder)
		}
	}

	private fun nameDeclMixin(element: VoileNameDeclMixin, holder: AnnotationHolder) {
		val parent = element.parent ?: return
		if (parent is VoileImplementation || parent is VoileSignature) {
			holder.createInfoAnnotation(element, null).textAttributes = VoileHighlighter.FUNCTION_NAME
		}
	}
}
