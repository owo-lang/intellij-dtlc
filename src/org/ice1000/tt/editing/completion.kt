package org.ice1000.tt.editing

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import javax.swing.Icon

class SimpleProvider(private val list: List<LookupElement>) :
	CompletionProvider<CompletionParameters>() {
	override fun addCompletions(
		parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) =
		list.forEach(result::addElement)
}

fun makeKeywordsCompletion(icon: Icon, vararg keywords: String) = makeKeywordsCompletion(icon, keywords.toList())
fun makeKeywordsCompletion(icon: Icon, keywords: List<String>) = keywords.map {
	LookupElementBuilder
		.create(it)
		.withTypeText("Keyword", true)
		.withIcon(icon)
		.bold()
}
