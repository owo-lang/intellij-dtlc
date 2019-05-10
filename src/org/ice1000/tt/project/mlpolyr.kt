package org.ice1000.tt.project

import com.intellij.openapi.project.Project
import org.ice1000.tt.TTBundle
import org.ice1000.tt.project.ui.OnlyExecutableProjectConfigurable

data class MLPolyRSettings(
	var exePath: String = "check"
)

class MLPolyRProjectConfigurable(project: Project) : OnlyExecutableProjectConfigurable() {
	private val settings = project.mlpolyrSettingsNullable?.settings ?: MLPolyRSettings()

	init {
		exePathField.text = settings.exePath
		configureMLPolyR(project)
	}

	override fun isModified() = exePathField.text != settings.exePath
	override fun getDisplayName() = TTBundle.message("mlpolyr.name")

	override fun apply() {
		settings.exePath = exePathField.text
	}
}
