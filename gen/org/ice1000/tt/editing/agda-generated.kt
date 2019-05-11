
package org.ice1000.tt.editing

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider
import com.intellij.psi.PsiFile
import org.ice1000.tt.*

class AgdaDefaultContext : TemplateContextType("AGDA_DEFAULT_CONTEXT_ID", AGDA_LANGUAGE_NAME, TTParentContext::class.java) {
	override fun isInContext(file: PsiFile, offset: Int) = file.fileType == AgdaFileType
}
