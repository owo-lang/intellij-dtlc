package org.ice1000.tt.gradle

import org.ice1000.tt.gradle.json.DEFAULT_PKG
import org.ice1000.tt.gradle.json.LangData
import org.intellij.lang.annotations.Language
import java.io.File

fun fileTypeFactory(langNames: Sequence<String>, outDir: File) {
	outDir.mkdirs()
	val className = "TTFileTypeFactory"
	// @Language("JAVA")
	val classContent = """
package $DEFAULT_PKG;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.project.DumbAware;

public final class $className extends FileTypeFactory implements DumbAware {
	@Override
	public void createFileTypes(FileTypeConsumer consumer) {
		${langNames.joinToString("\n\t\t") {
		"consumer.consume(${it}FileType.INSTANCE);"
	}}
	}
}
"""
	outDir.resolve("$className.java").writeText(classContent)
}

fun fileCreationGroup(langNames: Sequence<String>, outDir: File) {
	val outActionDir = outDir.resolve("action")
	outActionDir.mkdirs()
	val className = "NewTTActionGroup"
	// @Language("JAVA")
	val classContent = """
package $DEFAULT_PKG.action;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;
import $DEFAULT_PKG.TTBundle;

public final class $className extends DefaultActionGroup implements DumbAware {
	public $className() {
		super(${langNames.joinToString { "new New${it}File()" }});
		setPopup(true);
		getTemplatePresentation().setText(TTBundle.message("tt.actions.new-file-group"));
	}
}"""
	outActionDir.resolve("$className.java").writeText(classContent)
}

fun LangData.fileCreation(outDir: File) {
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

import static $basePackage.ConstantsKt.${constantPrefix}_LANGUAGE_NAME;

public class $fileCreationClassName extends NewTTFile {
	public $fileCreationClassName() {
		super(
			TTBundle.message("tt.actions.new-file.name", ${constantPrefix}_LANGUAGE_NAME),
			TTBundle.message("tt.actions.new-file.description", ${constantPrefix}_LANGUAGE_NAME),
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
			.setTitle(TTBundle.message("tt.actions.new-file.title", ${constantPrefix}_LANGUAGE_NAME))
			.addKind("File", TTIcons.${constantPrefix}_FILE, "$languageName File");
	}
}
"""
	outActionDir.resolve("$fileCreationClassName.java").writeText(fileCreationClassContent)
}
