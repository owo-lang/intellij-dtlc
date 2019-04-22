package org.ice1000.tt.editing

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiFile
import org.ice1000.tt.*

class OwODefaultContext : TemplateContextType(OWO_CONTEXT_ID, OWO_LANGUAGE_NAME) {
	override fun isInContext(file: PsiFile, offset: Int) = file.fileType == OwOFileType
}
