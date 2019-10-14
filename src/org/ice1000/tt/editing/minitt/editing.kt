package org.ice1000.tt.editing.minitt

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.BracePair
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.TTBundle
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.editing.TTCommenter
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.psi.minitt.*
import org.ice1000.tt.psi.minitt.MiniTTElementType.miniTTLexer

class MiniTTCommenter : TTCommenter() {
	override fun getLineCommentPrefix() = "-- "
}

class MiniTTBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(MiniTTTypes.LEFT_PAREN, MiniTTTypes.RIGHT_PAREN, false),
			BracePair(MiniTTTypes.LEFT_BRACE, MiniTTTypes.RIGHT_BRACE, false))
	}

	override fun getPairs() = PAIRS
}

class MiniTTFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(miniTTLexer(), MiniTTTokenType.IDENTIFIERS, MiniTTTokenType.COMMENTS, TokenSet.EMPTY)
}

class MiniTTAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is MiniTTConstructor -> constructor(element, holder)
			is MiniTTDeclaration -> declaration(element, holder)
			is MiniTTConstDeclaration -> constDeclaration(element, holder)
			is MiniTTPatternMatch -> patternMatch(element, holder)
			is MiniTTVariable -> variable(element, holder)
		}
	}

	private fun variable(element: MiniTTVariable, holder: AnnotationHolder) {
		val resolution = element.reference?.resolve()
		if (resolution == null) holder.createErrorAnnotation(element, TTBundle.message("tt.lint.unresolved")).apply {
			textAttributes = MiniTTHighlighter.UNRESOLVED
			highlightType = ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
		}
	}

	private fun constDeclaration(element: MiniTTConstDeclaration, holder: AnnotationHolder) {
		element.pattern?.let { pattern(it, holder) }
	}

	private fun pattern(pattern: MiniTTPattern, holder: AnnotationHolder) {
		when (pattern) {
			is MiniTTAtomPattern -> holder.createInfoAnnotation(pattern, null)
				.textAttributes = MiniTTHighlighter.FUNCTION_NAME
			is MiniTTPairPattern -> pattern.patternList.forEach { pattern(it, holder) }
		}
	}

	private fun declaration(element: MiniTTDeclaration, holder: AnnotationHolder) {
		element.pattern?.let { pattern(it, holder) }
	}

	private fun constructor(element: MiniTTConstructor, holder: AnnotationHolder) {
		if (element.parent is MiniTTBranches)
			holder.createInfoAnnotation(element.firstChild, null)
				.textAttributes = MiniTTHighlighter.CONSTRUCTOR_DECL
		else holder.createInfoAnnotation(element.firstChild, null)
			.textAttributes = MiniTTHighlighter.CONSTRUCTOR_CALL
	}

	private fun patternMatch(element: MiniTTPatternMatch, holder: AnnotationHolder) {
		holder.createInfoAnnotation(element.firstChild, null)
			.textAttributes = MiniTTHighlighter.CONSTRUCTOR_DECL
	}
}
