package org.ice1000.tt.action

import com.intellij.lang.Language
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
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

class HtmlExportAction : DefaultActionGroup(
	HtmlExportSingleFileAction,
	HtmlExportAlsoDependentFileAction
) {
	init {
		isPopup = true
	}

	override fun update(e: AnActionEvent) {
		e.presentation.isEnabledAndVisible = CommonDataKeys.PSI_FILE.getData(e.dataContext) is TTFile
	}
}

object HtmlExportSingleFileAction : AnAction("Single File") {
	override fun actionPerformed(e: AnActionEvent) {
		val project = e.project ?: return
		DumbService.getInstance(project).smartInvokeLater { performAction(e, project) }
	}

	private fun performAction(e: AnActionEvent, project: Project) {
		val file = CommonDataKeys.PSI_FILE.getData(e.dataContext)?.takeIf { it is TTFile } ?: return
		synchronized(HtmlExportSupport) {
			HtmlExportSupport.dependentFiles.clear()
			HtmlExportSupport.initBefore(file)
			ProgressManager.getInstance().run(object : Task.Backgroundable(project, "HTML Generation", true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
				override fun run(indicator: ProgressIndicator) {
					val out = file.virtualFile.canonicalPath?.let { "$it.html" } ?: return
					val startTime = System.currentTimeMillis()
					val language = file.language
					val vFile = HtmlExportSupport.work(out, language, indicator, file)
					val path = vFile?.canonicalPath?.removePrefix(project.guessProjectDir()?.canonicalPath.orEmpty())
					val s = "HTML Generated to $path in ${StringUtil.formatDuration(System.currentTimeMillis() - startTime)}"
					Notifications.Bus.notify(Notification("HTML Export", "", s, NotificationType.INFORMATION))
				}
			})
			HtmlExportSupport.after()
			HtmlExportSupport.dependentFiles.clear()
		}
	}

}

object HtmlExportAlsoDependentFileAction : AnAction("Along with Dependent Files") {
	private val alreadyGeneratedFiles = mutableSetOf<PsiFile>()

	override fun actionPerformed(e: AnActionEvent) {
		val project = e.project ?: return
		DumbService.getInstance(project).smartInvokeLater { performAction(e, project) }
	}

	private fun performAction(e: AnActionEvent, project: Project) {
		val file = CommonDataKeys.PSI_FILE.getData(e.dataContext)?.takeIf { it is TTFile } ?: return
		alreadyGeneratedFiles.clear()
		synchronized(HtmlExportSupport) {
			alreadyGeneratedFiles.add(file)
			HtmlExportSupport.dependentFiles.clear()
			ProgressManager.getInstance().run(object : Task.Backgroundable(project, "HTML Generation", true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
				override fun run(indicator: ProgressIndicator) {
					val startTime = System.currentTimeMillis()
					val language = file.language
					exportFor(indicator, language, file, project)
					val s = "${alreadyGeneratedFiles.size} HTML file(s) Generated in ${StringUtil.formatDuration(System.currentTimeMillis() - startTime)}"
					Notifications.Bus.notify(Notification("HTML Export", "", s, NotificationType.INFORMATION))
				}
			})
			HtmlExportSupport.dependentFiles.clear()
			alreadyGeneratedFiles.clear()
		}
	}

	private fun exportFor(indicator: ProgressIndicator, language: Language, file: PsiFile, project: Project?) {
		HtmlExportSupport.initBefore(file)
		val out = file.virtualFile.canonicalPath?.let { "$it.html" } ?: return
		HtmlExportSupport.work(out, language, indicator, file)
		HtmlExportSupport.after()
		(HtmlExportSupport.dependentFiles - alreadyGeneratedFiles).forEach {
			alreadyGeneratedFiles.add(it)
			exportFor(indicator, language, it, project)
		}
	}
}

object HtmlExportSupport {
	private lateinit var highlighter: SyntaxHighlighter
	private val idMap = TIntObjectHashMap<Info>()
	internal val dependentFiles = SmartList<PsiFile>()

	fun work(out: String, language: Language, indicator: ProgressIndicator, file: PsiFile): VirtualFile? {
		val ioFile = File(out)
		val ioCssFile = ioFile.parentFile.resolve("${language.displayName}.css")
		if (!ioCssFile.exists()) {
			val existingCss = javaClass.getResourceAsStream("/org/ice1000/tt/css/${language.displayName}.css")
			if (existingCss != null) ioCssFile.writeBytes(existingCss.readBytes())
		}
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
		VfsUtil.findFileByIoFile(ioCssFile, true)
		return VfsUtil.findFileByIoFile(ioFile, true)
	}

	// I didn't use `SyntaxTraverser` intentionally
	fun PRE.traverse(indicator: ProgressIndicator, element: PsiElement) {
		ProgressIndicatorProvider.checkCanceled()
		val elementType = element.elementType
		val tokenHighlights = highlighter.getTokenHighlights(elementType)
		val startOffset = element.startOffset
		val info = idMap[startOffset] ?: Info().also { idMap.put(startOffset, it) }
		if (tokenHighlights.isNotEmpty() && info.classes == null) info.classes = tokenHighlights.map { it.externalName }.toSet()
		if (info.href == null || info.href == "??") info.href = element.reference?.let { reference ->
			val resolved = reference.resolve() ?: return@let "??"
			val resolvedFile = resolved.containingFile
			if (resolvedFile !in dependentFiles) dependentFiles.add(resolvedFile)
			if (resolvedFile == element.containingFile) "#${resolved.startOffset}"
			else "${resolvedFile.virtualFile.name}.html#${resolved.startOffset}"
		}

		if (element.firstChild != null)
			element.childrenWithLeaves.forEach { traverse(indicator, it) }
		else a {
			val infoClasses = info.classes
			if (infoClasses != null) classes = infoClasses
			val infoHref = info.href
			val text = element.text
			if (intoHref == "??") {
				title = "Failed to find the declaration of $text"
				indicator.text = "Token $text (Failed)"
			} else if (infoHref != null) {
				href = infoHref
				title = "Goto the declaration of $text"
				indicator.text = "Token $text"
			}
			id = "$startOffset"
			+text
		}
	}

	fun initBefore(file: PsiFile) {
		idMap.clear()
		val language = file.language
		highlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(language, file.project, file.virtualFile)
	}

	fun after() = idMap.clear()
}
