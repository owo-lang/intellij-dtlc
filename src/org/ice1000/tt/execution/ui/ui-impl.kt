package org.ice1000.tt.execution.ui

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import org.ice1000.tt.MiniTTBundle
import org.ice1000.tt.MiniTTFileType
import org.ice1000.tt.execution.MiniTTRunConfiguration
import org.jetbrains.annotations.Contract
import java.nio.file.Files
import java.nio.file.Paths
import javax.naming.ConfigurationException

class MiniTTRunConfigurationEditorImpl(configuration: MiniTTRunConfiguration, project: Project) : MiniTTRunConfigurationEditor() {
	override fun createEditor() = mainPanel

	init {
		workingDirField.addBrowseFolderListener(MiniTTBundle.message("minitt.ui.run-config.select-working-dir"),
			MiniTTBundle.message("minitt.ui.run-config.select-working-dir.description"),
			project,
			FileChooserDescriptorFactory.createSingleFolderDescriptor())
		targetFileField.addBrowseFolderListener(MiniTTBundle.message("minitt.ui.run-config.select-minitt-file"),
			MiniTTBundle.message("minitt.ui.run-config.select-minitt-file.description"),
			project,
			FileChooserDescriptorFactory.createSingleFileDescriptor(MiniTTFileType))
		exePathField.addBrowseFolderListener(MiniTTBundle.message("minitt.ui.project.select-compiler"),
			MiniTTBundle.message("minitt.ui.project.select-compiler.description"),
			project,
			FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor())
		resetEditorFrom(configuration)
	}

	override fun resetEditorFrom(s: MiniTTRunConfiguration) {
		workingDirField.text = s.workingDir
		targetFileField.text = s.targetFile
		exePathField.text = s.minittExecutable
		additionalArgumentsField.text = s.additionalOptions
	}

	@Throws(ConfigurationException::class)
	override fun applyEditorTo(s: MiniTTRunConfiguration) {
		val targetFile = targetFileField.text
		if (Files.isReadable(Paths.get(targetFile))) s.targetFile = targetFile
		else reportInvalidPath(targetFile)
		val workingDirectory = workingDirField.text
		if (Files.isDirectory(Paths.get(workingDirectory))) s.workingDir = workingDirectory
		else reportInvalidPath(workingDirectory)
		s.minittExecutable = exePathField.text
		s.additionalOptions = additionalArgumentsField.text
	}

	@Contract("_ -> fail")
	@Throws(ConfigurationException::class)
	private fun reportInvalidPath(path: String) {
		throw ConfigurationException(MiniTTBundle.message("minitt.ui.run-config.invalid-path", path))
	}
}
