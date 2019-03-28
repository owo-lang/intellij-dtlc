package org.ice1000.tt.project.ui

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import org.ice1000.tt.TTBundle
import org.ice1000.tt.project.MiniTTSettings
import org.ice1000.tt.project.minittPath
import org.ice1000.tt.project.minittSettingsNullable
import org.ice1000.tt.versionOf

class MiniTTProjectConfigurableImpl(project: Project) : MiniTTProjectConfigurable() {
	// For building searchable options
	private val settings = project.minittSettingsNullable?.settings ?: MiniTTSettings()

	init {
		exePathField.text = settings.exePath
		exePathField.addBrowseFolderListener(TTBundle.message("minitt.ui.project.select-compiler"),
			TTBundle.message("minitt.ui.project.select-compiler.description"),
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

	override fun isModified() = exePathField.text != settings.exePath || versionLabel.text != settings.version
	override fun getDisplayName() = TTBundle.message("minitt.name")
	override fun createComponent() = mainPanel
	override fun apply() {
		settings.exePath = exePathField.text
		settings.version = versionLabel.text
	}
}
