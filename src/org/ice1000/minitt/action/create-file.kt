package org.ice1000.minitt.action

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.actions.AttributesDefaults
import com.intellij.ide.fileTemplates.ui.CreateFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiDirectory
import icons.MiniTTIcons
import org.ice1000.minitt.MiniTTBundle
import java.util.*

class NewMiniTTFile : CreateFileFromTemplateAction(
	MiniTTBundle.message("minitt.actions.new-file.name"),
	MiniTTBundle.message("minitt.actions.new-file.description"),
	MiniTTIcons.MINI_TT_FILE), DumbAware {
	companion object {
		fun createProperties(project: Project, className: String): Properties {
			val properties = FileTemplateManager.getInstance(project).defaultProperties
			properties += "NAME" to className
			properties += "NAME_SNAKE" to className.toLowerCase().replace(Regex("[ \r\t-()!@#~]+"), "_")
			return properties
		}
	}

	override fun getActionName(directory: PsiDirectory?, s: String, s2: String?) =
		MiniTTBundle.message("minitt.actions.new-file.name")

	override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
		builder
			.setTitle(MiniTTBundle.message("minitt.actions.new-file.title"))
			.addKind("File", MiniTTIcons.MINI_TT_FILE, "MiniTT File")
	}

	override fun createFileFromTemplate(name: String, template: FileTemplate, dir: PsiDirectory) = try {
		val className = FileUtilRt.getNameWithoutExtension(name)
		val project = dir.project
		val properties = createProperties(project, className)
		CreateFromTemplateDialog(project, dir, template, AttributesDefaults(className).withFixedName(true), properties)
			.create()
			.containingFile
	} catch (e: Exception) {
		LOG.error("Error while creating new file", e)
		null
	}
}
