package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language

fun LanguageUtilityGenerationTask.fileCreation(nickname: String) {
	@Language("kotlin")
	val fileCreation = """
package $basePackage.action

import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import icons.TTIcons
import org.ice1000.tt.TTBundle

object New${languageName}File : NewTTFile(
	TTBundle.message("$nickname.actions.new-file.name"),
	TTBundle.message("$nickname.actions.new-file.description"),
	TTIcons.${constantPrefix}_FILE) {
	override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
		builder
			.setTitle(TTBundle.message("$nickname.actions.new-file.title"))
			.addKind("File", TTIcons.${constantPrefix}_FILE, "$languageName File")
	}
}
"""
	outDir.resolve("action")
		.apply { mkdirs() }
		.resolve("$nickname-generated.kt").writeText(fileCreation)
}
