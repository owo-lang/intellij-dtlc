package org.ice1000.tt.execution.ui

import com.intellij.execution.configurations.RunProfileState
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import org.ice1000.tt.MiniTTFileType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.execution.InterpretedRunConfiguration
import org.ice1000.tt.execution.MiniTTRunConfiguration
import org.jetbrains.annotations.Contract
import java.nio.file.Files
import java.nio.file.Paths
import javax.naming.ConfigurationException

abstract class InterpretedRunConfigurationEditorImpl<T : InterpretedRunConfiguration<out RunProfileState>> : InterpretedRunConfigurationEditor<T>() {
	override fun createEditor() = mainPanel
	override fun resetEditorFrom(s: T) {
		workingDirField.text = s.workingDir
		targetFileField.text = s.targetFile
		additionalArgumentsField.text = s.additionalOptions
	}

	@Throws(ConfigurationException::class)
	override fun applyEditorTo(s: T) {
		val targetFile = targetFileField.text
		if (Files.isReadable(Paths.get(targetFile))) s.targetFile = targetFile
		else reportInvalidPath(targetFile)
		val workingDirectory = workingDirField.text
		if (Files.isDirectory(Paths.get(workingDirectory))) s.workingDir = workingDirectory
		else reportInvalidPath(workingDirectory)
		s.additionalOptions = additionalArgumentsField.text
	}

	@Contract("_ -> fail")
	@Throws(ConfigurationException::class)
	protected fun reportInvalidPath(path: String) {
		throw ConfigurationException(TTBundle.message("minitt.ui.run-config.invalid-path", path))
	}
}

class MiniTTRunConfigurationEditor(
	configuration: MiniTTRunConfiguration,
	project: Project
) : InterpretedRunConfigurationEditorImpl<MiniTTRunConfiguration>() {
	init {
		workingDirField.addBrowseFolderListener(TTBundle.message("minitt.ui.run-config.select-working-dir"),
			TTBundle.message("minitt.ui.run-config.select-working-dir.description"),
			project,
			FileChooserDescriptorFactory.createSingleFolderDescriptor())
		targetFileField.addBrowseFolderListener(TTBundle.message("minitt.ui.run-config.select-minitt-file"),
			TTBundle.message("minitt.ui.run-config.select-minitt-file.description"),
			project,
			FileChooserDescriptorFactory.createSingleFileDescriptor(MiniTTFileType))
		exePathField.addBrowseFolderListener(TTBundle.message("minitt.ui.project.select-compiler"),
			TTBundle.message("minitt.ui.project.select-compiler.description"),
			project,
			FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor())
		resetEditorFrom(configuration)
	}

	override fun resetEditorFrom(s: MiniTTRunConfiguration) {
		super.resetEditorFrom(s)
		exePathField.text = s.minittExecutable
	}

	override fun applyEditorTo(s: MiniTTRunConfiguration) {
		super.applyEditorTo(s)
		s.minittExecutable = exePathField.text
	}
}
