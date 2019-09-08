package org.ice1000.tt.action

import com.google.common.base.CaseFormat
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.actions.AttributesDefaults
import com.intellij.ide.fileTemplates.ui.CreateFromTemplateDialog
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiDirectory
import icons.TTIcons
import org.ice1000.tt.TTBundle
import java.util.*
import javax.swing.Icon

fun createProperties(project: Project, className: String): Properties {
	val properties = FileTemplateManager.getInstance(project).defaultProperties
	properties += "NAME" to className
	val snakeCase = className.toLowerCase().replace(Regex("[ \\r\\t\\-()!@#~]+"), "_")
	properties += "NAME_SNAKE" to snakeCase
	properties += "NAME_CAMEL" to CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, snakeCase)
	return properties
}

abstract class NewTTFile(private val name: String, description: String, icon: Icon) : CreateFileFromTemplateAction(name, description, icon), DumbAware {
	override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?) = name

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

private object NewOwOFile : NewTTFile(
	TTBundle.message("owo.actions.new-file.name"),
	TTBundle.message("owo.actions.new-file.description"),
	TTIcons.OWO_FILE) {
	override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
		builder
			.setTitle(TTBundle.message("owo.actions.new-file.title"))
			.addKind("File", TTIcons.OWO_FILE, "OwO File")
	}
}

class NewTTActionGroup : DefaultActionGroup(
	NewOwOFile,
	NewVoileFile,
	NewACoreFile,
	NewMiniTTFile,
	NewAgdaFile,
	NewRedPrlFile,
	NewCubicalTTFile,
	NewYaccTTFile,
	NewMlangFile,
	NewMLPolyRFile
), DumbAware {
	init {
		isPopup = true
		templatePresentation.text = TTBundle.message("tt.actions.new-file-group")
	}
}
