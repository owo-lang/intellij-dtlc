package org.ice1000.tt.editing

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider
import com.intellij.psi.PsiFile
import org.ice1000.tt.*

class MiniTTDefaultContext : TemplateContextType(MINI_TT_DEFAULT_CONTEXT_ID, MINI_TT_LANGUAGE_NAME) {
	override fun isInContext(file: PsiFile, offset: Int) = file.fileType == MiniTTFileType
}

class OwODefaultContext : TemplateContextType(OWO_CONTEXT_ID, OWO_LANGUAGE_NAME) {
	override fun isInContext(file: PsiFile, offset: Int) = file.fileType == OwOFileType
}

class MiniTTLiveTemplateProvider : DefaultLiveTemplatesProvider {
	private companion object DefaultHolder {
		private val DEFAULT = arrayOf("/liveTemplates/MiniTT")
	}

	override fun getDefaultLiveTemplateFiles() = DEFAULT
	override fun getHiddenLiveTemplateFiles(): Array<String>? = null
}
