package org.ice1000.tt.editing.cubicaltt

import com.intellij.codeInsight.completion.*
import com.intellij.openapi.project.DumbAware
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import icons.TTIcons
import org.ice1000.tt.editing.SimpleProvider
import org.ice1000.tt.editing.makeKeywordsCompletion
import org.ice1000.tt.psi.childrenWithLeaves
import org.ice1000.tt.psi.cubicaltt.CubicalCompletionElement
import org.ice1000.tt.psi.cubicaltt.CubicalTTFileImpl
import org.ice1000.tt.psi.cubicaltt.CubicalTTImportMixin
import org.ice1000.tt.psi.cubicaltt.CubicalTTModuleMixin

class CubicalTTCompletionContributor : CompletionContributor(), DumbAware {
	private val keywords = makeKeywordsCompletion(TTIcons.CUBICAL_TT, listOf(
		"transparentAll", "transparent",
		"transport", "undefined", "import", "module", "mutual", "opaque",
		"split@", "unglue", "hComp", "hdata", "PathP", "split", "where", "comp",
		"data", "fill", "glue", "Glue", "with", "id", "id", "let", "Id", "in", "U"
	))

	init {
		extend(CompletionType.BASIC, psiElement(), SimpleProvider(keywords))
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
