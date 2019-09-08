package org.ice1000.tt.editing.mlang

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.lang.BracePair
import com.intellij.openapi.project.DumbAware
import com.intellij.patterns.PlatformPatterns
import icons.TTIcons
import org.ice1000.tt.editing.SimpleProvider
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.editing.makeKeywordsCompletion
import org.ice1000.tt.psi.mlang.MlangTypes

class MlangBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(MlangTypes.LBRACE, MlangTypes.RBRACE, false),
			BracePair(MlangTypes.LBRACK, MlangTypes.RBRACK, false),
			BracePair(MlangTypes.LPAREN, MlangTypes.RPAREN, false))
	}

	override fun getPairs() = PAIRS
}

class MlangCompletionContributor : CompletionContributor(), DumbAware {
	private val keywords = makeKeywordsCompletion(TTIcons.M_LANG, listOf(
		"as", "run", "sum", "case", "type", "make", "field", "define", "record",
		"transp", "unglue", "__debug", "declare", "parameters", "inductively",
		"glue", "glue_type", "hfill", "hcomp", "fill", "comp",
		"with_constructor"
	))

	init {
		extend(CompletionType.BASIC, PlatformPatterns.psiElement(), SimpleProvider(keywords))
	}
}
