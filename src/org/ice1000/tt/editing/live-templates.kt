package org.ice1000.tt.editing

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiFile
import org.ice1000.tt.*

const val TT_PARENT_CONTEXT_ID = "TTParentContext"

class TTParentContext : TemplateContextType(TT_PARENT_CONTEXT_ID, "Type Theory") {
	override fun isInContext(file: PsiFile, offset: Int) = false
}

class OwODefaultContext : TemplateContextType(OWO_CONTEXT_ID, OWO_LANGUAGE_NAME, TTParentContext::class.java) {
	override fun isInContext(file: PsiFile, offset: Int) = file.fileType == OwOFileType
}
