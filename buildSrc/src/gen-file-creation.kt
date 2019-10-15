package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language
import java.io.File

fun LangData.fileCreation(nickname: String, outDir: File) {
	val outActionDir = outDir.resolve("action")
	outActionDir.mkdirs()

	val fileCreationClassName = "New${languageName}File"
	@Language("JAVA")
	val fileCreationClassContent = """
package $basePackage.action;

import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import icons.TTIcons;
import $basePackage.TTBundle;
import org.jetbrains.annotations.NotNull;

public class $fileCreationClassName extends NewTTFile {
	public $fileCreationClassName() {
		super(
			TTBundle.message("$nickname.actions.new-file.name"), 
			TTBundle.message("$nickname.actions.new-file.description"),
			TTIcons.${constantPrefix}_FILE
		);
	}

	@Override
	public void buildDialog(
		@NotNull Project project,
		@NotNull PsiDirectory directory,
		@NotNull CreateFileFromTemplateDialog.Builder builder
	) {
		builder
			.setTitle(TTBundle.message("$nickname.actions.new-file.title"))
			.addKind("File", TTIcons.${constantPrefix}_FILE, "$languageName File");
	}
}
"""
	outActionDir.resolve("$fileCreationClassName.java").writeText(fileCreationClassContent)
}
