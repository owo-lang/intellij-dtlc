package org.ice1000.tt.editing.yacctt

import com.intellij.codeInsight.completion.*
import com.intellij.openapi.project.DumbAware
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import icons.TTIcons
import org.ice1000.tt.editing.SimpleProvider
import org.ice1000.tt.editing.makeKeywordsCompletion
import org.ice1000.tt.psi.childrenWithLeaves
import org.ice1000.tt.psi.yacctt.YaccCompletionElement
import org.ice1000.tt.psi.yacctt.YaccTTFileImpl
import org.ice1000.tt.psi.yacctt.YaccTTImportMixin
import org.ice1000.tt.psi.yacctt.YaccTTModuleMixin

class YaccTTCompletionContributor : CompletionContributor(), DumbAware {
	private val keywords = makeKeywordsCompletion(TTIcons.CUBICAL_TT, listOf(
		"transparentAll", "transparent", "undefined", "import", "module",
		"mutual", "opaque", "split@", "hdata", "PathP", "LineP", "split", "Vproj",
		"where", "data", "with", "hcom", "Vin", "let", "coe", "com", "box", "cap", "in"
	))

	init {
		extend(CompletionType.BASIC, psiElement(), SimpleProvider(keywords))
		extend(CompletionType.BASIC, psiElement(), object : CompletionProvider<CompletionParameters>() {
			override fun addCompletions(
				parameters: CompletionParameters,
				context: ProcessingContext,
				result: CompletionResultSet
			) {
				val file = parameters.originalFile as? YaccTTFileImpl ?: return
				val module = file.module ?: return
				module.childrenWithLeaves
					.filterIsInstance<YaccTTImportMixin>()
					.mapNotNull { it.moduleUsage }
					.mapNotNull { it.reference?.resolve() }
					.filterIsInstance<YaccTTModuleMixin>()
					.mapNotNull { it.stub }
					.flatMap {
						it.childrenStubs.asSequence().filterIsInstance<YaccCompletionElement>()
					}.forEach { result.addElement(it.lookupElement) }
			}
		})
	}
}
