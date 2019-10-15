package org.ice1000.tt.editing.cubicaltt

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import org.ice1000.tt.psi.childrenWithLeaves
import org.ice1000.tt.psi.cubicaltt.CubicalCompletionElement
import org.ice1000.tt.psi.cubicaltt.CubicalTTFileImpl
import org.ice1000.tt.psi.cubicaltt.CubicalTTImportMixin
import org.ice1000.tt.psi.cubicaltt.CubicalTTModuleMixin

class CubicalTTSmartCompletionContributor : CubicalTTCompletionContributor() {
	init {
		extend(CompletionType.BASIC, psiElement(), object : CompletionProvider<CompletionParameters>() {
			override fun addCompletions(
				parameters: CompletionParameters,
				context: ProcessingContext,
				result: CompletionResultSet
			) {
				val file = parameters.originalFile as? CubicalTTFileImpl ?: return
				val module = file.module ?: return
				module.childrenWithLeaves
					.filterIsInstance<CubicalTTImportMixin>()
					.mapNotNull { it.moduleUsage }
					.mapNotNull { it.reference?.resolve() }
					.filterIsInstance<CubicalTTModuleMixin>()
					.mapNotNull { it.stub }
					.flatMap {
						it.childrenStubs.asSequence().filterIsInstance<CubicalCompletionElement>()
					}.forEach { result.addElement(it.lookupElement) }
			}
		})
	}
}
