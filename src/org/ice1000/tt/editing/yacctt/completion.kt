package org.ice1000.tt.editing.yacctt

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import org.ice1000.tt.psi.childrenWithLeaves
import org.ice1000.tt.psi.yacctt.YaccCompletionElement
import org.ice1000.tt.psi.yacctt.YaccTTFileImpl
import org.ice1000.tt.psi.yacctt.YaccTTImportMixin
import org.ice1000.tt.psi.yacctt.YaccTTModuleMixin

class YaccTTSmartCompletionContributor : YaccTTCompletionContributor() {
	init {
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
