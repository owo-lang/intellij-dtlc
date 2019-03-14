package org.ice1000.minitt.project.ui

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import org.ice1000.minitt.MiniTTBundle
import org.ice1000.minitt.project.MiniTTSettings
import org.ice1000.minitt.project.minittPath
import org.ice1000.minitt.project.minittSettings
import org.ice1000.minitt.project.minittSettingsNullable
import org.ice1000.minitt.versionOf

class MiniTTProjectConfigurableImpl(project: Project) : MiniTTProjectConfigurable() {
	// For building searchable options
	private val settings = project.minittSettingsNullable?.settings ?: MiniTTSettings()

	init {
		exePathField.text = settings.exePath
		exePathField.addBrowseFolderListener(MiniTTBundle.message("minitt.ui.project.select-compiler"),
			MiniTTBundle.message("minitt.ui.project.select-compiler.description"),
			project,
			FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor())
		exePathField.addPropertyChangeListener { reinit() }
		versionLabel.text = settings.version
		guessExeButton.addActionListener {
			minittPath?.let { exePathField.text = it }
		}
	}

	private fun reinit() {
		versionLabel.text = versionOf(exePathField.text)
	}

	override fun isModified() = exePathField.text != settings.exePath
	override fun getDisplayName() = MiniTTBundle.message("minitt.name")
	override fun createComponent() = mainPanel
	override fun apply() {
		settings.exePath = exePathField.text
		settings.version = versionLabel.text
	}
}
