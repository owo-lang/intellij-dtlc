package org.ice1000.tt.editing.voile

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.lang.BracePair
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.openapi.project.DumbAware
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import icons.TTIcons
import org.ice1000.tt.editing.SimpleProvider
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.editing.makeKeywordsCompletion
import org.ice1000.tt.psi.voile.*
import org.ice1000.tt.psi.voile.VoileElementType.voileLexer

class VoileBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(VoileTypes.LBRACE, VoileTypes.RBRACE, false),
			BracePair(VoileTypes.LBRACE2, VoileTypes.RBRACE2, false),
			BracePair(VoileTypes.LPAREN, VoileTypes.RPAREN, false))
	}

	override fun getPairs() = PAIRS
}

class VoileFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(voileLexer(), VoileTokenType.IDENTIFIERS, VoileTokenType.COMMENTS, TokenSet.EMPTY)
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

class VoileCompletionContributor : CompletionContributor(), DumbAware {
	private val keywords = makeKeywordsCompletion(TTIcons.VOILE, listOf(
		"Sum", "Rec", "Type", "let", "val", "case", "or", "whatever"
	))

	init {
		extend(CompletionType.BASIC, PlatformPatterns.psiElement(), SimpleProvider(keywords))
	}
}
