package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language

fun LanguageUtilityGenerationTask.editing(nickname: String) {
	@Language("kotlin")
	val editing = """
	package $basePackage.editing

	import com.intellij.codeInsight.template.TemplateContextType
	import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider
	import com.intellij.psi.PsiFile
	import org.ice1000.tt.*

	class ${languageName}DefaultContext : TemplateContextType("${constantPrefix}_DEFAULT_CONTEXT_ID", ${constantPrefix}_LANGUAGE_NAME, TTParentContext::class.java) {
		override fun isInContext(file: PsiFile, offset: Int) = file.fileType == ${languageName}FileType
	}
	"""
	outDir.resolve("editing")
		.apply { mkdirs() }
		.resolve("$nickname-generated.kt").writeText(editing)
}
