package org.ice1000.tt.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.psi.PsiElement
import com.intellij.util.containers.IntToIntSetMap
import gnu.trove.TIntHashSet
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.ice1000.tt.TTFile
import org.ice1000.tt.psi.childrenWithLeaves
import org.ice1000.tt.psi.elementType
import org.ice1000.tt.psi.startOffset
import java.io.File

class HtmlExportAction : AnAction() {
	lateinit var highlighter: SyntaxHighlighter
	val idSet = TIntHashSet()

	override fun update(e: AnActionEvent) {
		e.presentation.isEnabledAndVisible = CommonDataKeys.PSI_FILE.getData(e.dataContext) is TTFile
	}

	override fun actionPerformed(e: AnActionEvent) {
		idSet.clear()
		val file = CommonDataKeys.PSI_FILE.getData(e.dataContext)?.takeIf { it is TTFile } ?: return
		highlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(file.language, e.project, file.virtualFile)
		val out = file.virtualFile.canonicalPath?.let { "$it.html" } ?: return
		File(out).writer().use {
			it.appendHTML().pre { traverse(file) }
			it.flush()
		}
		idSet.clear()
	}

	private fun PRE.traverse(element: PsiElement): Unit = a {
		val elementType = element.elementType
		classes = highlighter.getTokenHighlights(elementType).map { it.externalName }.toSet()
		val startOffset = element.startOffset
		if (startOffset !in idSet) {
			id = "$startOffset"
			idSet.add(startOffset)
		}
		if (element.firstChild != null)
			element.childrenWithLeaves.forEach { this@traverse.traverse(it) }
		else +element.text
	}
}
