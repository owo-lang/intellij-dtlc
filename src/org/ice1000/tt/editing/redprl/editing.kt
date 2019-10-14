package org.ice1000.tt.editing.redprl

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.lang.BracePair
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.openapi.project.DumbAware
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.tree.TokenSet
import icons.TTIcons
import org.ice1000.tt.editing.SimpleProvider
import org.ice1000.tt.editing.TTBraceMatcher
import org.ice1000.tt.editing.TTFindUsagesProvider
import org.ice1000.tt.editing.makeKeywordsCompletion
import org.ice1000.tt.psi.redprl.RedPrlElementType.redPrlLexer
import org.ice1000.tt.psi.redprl.RedPrlTokenType
import org.ice1000.tt.psi.redprl.RedPrlTypes

class RedPrlBraceMatcher : TTBraceMatcher() {
	private companion object Pairs {
		private val PAIRS = arrayOf(
			BracePair(RedPrlTypes.LPAREN, RedPrlTypes.RPAREN, false),
			BracePair(RedPrlTypes.LSQUARE, RedPrlTypes.RSQUARE, false),
			BracePair(RedPrlTypes.LBRACKET, RedPrlTypes.RBRACKET, false))
	}

	override fun getPairs() = PAIRS
}

class RedPrlFindUsagesProvider : TTFindUsagesProvider() {
	override fun getWordsScanner() = DefaultWordsScanner(redPrlLexer(), RedPrlTokenType.IDENTIFIERS, RedPrlTokenType.COMMENTS, TokenSet.EMPTY)
}

class RedPrlCompletionContributor : CompletionContributor(), DumbAware {
	// Regular expression:
	// s/([a-zA-Z\-]+) \{ return [A-Z_]+; }/"$1",/rg
	private val keywords = makeKeywordsCompletion(TTIcons.RED_PRL, listOf(
		"pushout-rec", "assumption",
		"auto-step", "inversion", "coeq-rec", "progress", "symmetry", "discrete",
		"nat-rec", "negsucc", "int-rec", "pushout", "without", "extract",
		"theorem", "rewrite", "record", "refine", "define", "tactic", "repeat",
		"reduce", "unfold", "tuple", "right", "cecod", "cedom", "Vproj", "claim",
		"exact", "match", "query", "concl", "print", "fcom", "bool", "zero",
		"succ", "void", "base", "loop", "path", "line", "left", "glue", "coeq",
		"self", "ecom", "hcom", "then", "else", "with", "case", "lmax", "quit",
		"data", "fail", "auto", "elim", "true", "type", "nat", "int", "pos",
		"lam", "rec", "mem", "box", "cap", "Vin", "abs", "com", "coe", "let",
		"use", "dim", "lvl", "knd", "exp", "tac", "jdg", "val", "kan", "pre",
		"ax", "tt", "ff", "if", "ni", "of", "by", "in", "fn", "id", "at", "V", "U"
	))

	init {
		extend(CompletionType.BASIC, PlatformPatterns.psiElement(), SimpleProvider(keywords))
	}
}
