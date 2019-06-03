package org.ice1000.tt.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.progress.*
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiElement
import gnu.trove.TIntObjectHashMap
import kotlinx.html.PRE
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.pre
import kotlinx.html.stream.appendHTML
import org.ice1000.tt.TTFile
import org.ice1000.tt.psi.childrenWithLeaves
import org.ice1000.tt.psi.elementType
import org.ice1000.tt.psi.startOffset
import java.io.File

private data class Info(
	var classes: Set<String>? = null,
	var href: String? = null
)

class HtmlExportAction : AnAction() {
	lateinit var highlighter: SyntaxHighlighter
	private val idMap = TIntObjectHashMap<Info>()

	override fun update(e: AnActionEvent) {
		e.presentation.isEnabledAndVisible = CommonDataKeys.PSI_FILE.getData(e.dataContext) is TTFile
	}

	override fun actionPerformed(e: AnActionEvent) {
		idMap.clear()
		val file = CommonDataKeys.PSI_FILE.getData(e.dataContext)?.takeIf { it is TTFile } ?: return
		val project = e.project
		highlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(file.language, project, file.virtualFile)
		val out = file.virtualFile.canonicalPath?.let { "$it.html" } ?: return
		ProgressManager.getInstance().run (object : Task.Backgroundable(project, "HTML Generation", true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
			override fun run(indicator: ProgressIndicator) {
				val ioFile = File(out)
				ioFile.writer().use {
					it.appendHTML().pre { traverse(file) }
					it.flush()
				}
				VfsUtil.findFileByIoFile(ioFile, true)
			}
		})
		idMap.clear()
	}

	private fun PRE.traverse(element: PsiElement) {
		ProgressIndicatorProvider.checkCanceled()
		val elementType = element.elementType
		val tokenHighlights = highlighter.getTokenHighlights(elementType)
		val startOffset = element.startOffset
		val info = idMap[startOffset] ?: Info().also { idMap.put(startOffset, it) }
		if (tokenHighlights.isNotEmpty() && info.classes == null) info.classes = tokenHighlights.map { it.externalName }.toSet()
		if (info.href == null) info.href = element.reference?.resolve()?.let { resolved ->
			// Support cross-file reference?
			if (resolved.containingFile == element.containingFile)
				"#${resolved.startOffset}"
			else null
		}

		if (element.firstChild != null)
			element.childrenWithLeaves.forEach { this@traverse.traverse(it) }
		else a {
			val infoClasses = info.classes
			if (infoClasses != null) classes = infoClasses
			val infoHref = info.href
			if (infoHref != null) href = infoHref
			+element.text
		}
	}
}
