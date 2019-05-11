
package org.ice1000.tt.editing

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider
import com.intellij.psi.PsiFile
import org.ice1000.tt.*

class AgdaDefaultContext : TemplateContextType("AGDA_DEFAULT_CONTEXT_ID", AGDA_LANGUAGE_NAME) {
	override fun isInContext(file: PsiFile, offset: Int) = file.fileType == AgdaFileType
}

class AgdaLiveTemplateProvider : DefaultLiveTemplatesProvider {
	private companion object DefaultHolder {
		private val DEFAULT = arrayOf("/liveTemplates/Agda")
	}

	override fun getDefaultLiveTemplateFiles() = DEFAULT
	override fun getHiddenLiveTemplateFiles(): Array<String>? = null
}
