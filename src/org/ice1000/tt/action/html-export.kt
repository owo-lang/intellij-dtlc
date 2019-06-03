package org.ice1000.tt.action

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiElement
import gnu.trove.TIntObjectHashMap
import kotlinx.html.*
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

private val LOG_GROUP = NotificationGroup.logOnlyGroup("Dependently-Typed Lambda Calculus")

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
		val language = file.language
		highlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(language, project, file.virtualFile)
		val out = file.virtualFile.canonicalPath?.let { "$it.html" } ?: return
		ProgressManager.getInstance().run(object : Task.Backgroundable(project, "HTML Generation", true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
			override fun run(indicator: ProgressIndicator) {
				val startTime = System.currentTimeMillis()
				val ioFile = File(out)
				val ioCssFile = ioFile.parentFile.resolve("${language.displayName}.css")
				val existingCss = javaClass.getResourceAsStream("/org/ice1000/tt/css/${language.displayName}.css")
				if (existingCss != null && !ioCssFile.exists()) ioCssFile.writeBytes(existingCss.readAllBytes())
				ioFile.writer().use {
					it.appendHTML().html {
						head { link(rel = "stylesheet", type = "text/css", href = ioCssFile.name) }
						body {
							pre {
								classes = setOf(language.displayName)
								ReadAction.run<ProcessCanceledException> { traverse(indicator, file) }
							}
						}
					}
					it.flush()
				}
				val vFile = VfsUtil.findFileByIoFile(ioFile, true)
				VfsUtil.findFileByIoFile(ioCssFile, true)
				val path = vFile?.canonicalPath?.removePrefix(project?.guessProjectDir()?.canonicalPath.orEmpty())
				val s = "HTML Generated to $path in ${StringUtil.formatDuration(System.currentTimeMillis() - startTime)}"
				Notifications.Bus.notify(Notification("HTML Export", "", s, NotificationType.INFORMATION))
			}
		})
		idMap.clear()
	}

	// I didn't use `SyntaxTraverser` intentionally
	private fun PRE.traverse(indicator: ProgressIndicator, element: PsiElement) {
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
			element.childrenWithLeaves.forEach { traverse(indicator, it) }
		else a {
			val infoClasses = info.classes
			if (infoClasses != null) classes = infoClasses
			val infoHref = info.href
			if (infoHref != null) href = infoHref
			id = "$startOffset"
			val text = element.text
			indicator.text = "Token $text"
			+text
		}
	}
}
