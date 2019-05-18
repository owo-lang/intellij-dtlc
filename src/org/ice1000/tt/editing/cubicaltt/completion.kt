package org.ice1000.tt.editing.cubicaltt

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.DumbAware
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import icons.TTIcons
import org.ice1000.tt.editing.SimpleProvider
import org.ice1000.tt.psi.childrenWithLeaves
import org.ice1000.tt.psi.cubicaltt.*

class CubicalTTCompletionContributor : CompletionContributor(), DumbAware {
	private val keywords = listOf(
		"transparentAll", "transparent",
		"transport", "undefined", "import", "module", "mutual", "opaque",
		"split@", "unglue", "hComp", "hdata", "PathP", "split", "where", "comp",
		"data", "fill", "glue", "Glue", "with", "id", "id", "let", "Id", "in", "U"
	).map {
		LookupElementBuilder
			.create(it)
			.withTypeText("Keyword")
			.withIcon(TTIcons.CUBICAL_TT)
			.bold()
	}

	init {
		extend(CompletionType.BASIC, psiElement(), SimpleProvider(keywords))
		extend(CompletionType.BASIC, psiElement(CubicalTTTypes.NAME_EXP), object : CompletionProvider<CompletionParameters>() {
			override fun addCompletions(
				parameters: CompletionParameters,
				context: ProcessingContext,
				result: CompletionResultSet
			) {
				val file = parameters.originalFile as? CubicalTTFileImpl ?: return
				file.childrenWithLeaves
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
