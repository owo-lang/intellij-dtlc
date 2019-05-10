package org.ice1000.tt.project

import com.intellij.openapi.project.Project
import org.ice1000.tt.TTBundle
import org.ice1000.tt.project.ui.OnlyExecutableProjectConfigurableImpl

data class MLPolyRSettings(
	var exePath: String = "check"
)

class MLPolyRProjectConfigurable(project: Project) : OnlyExecutableProjectConfigurableImpl() {
	private val settings = project.mlpolyrSettingsNullable?.settings ?: MLPolyRSettings()

	init {
		exePathField.text = settings.exePath
		configureMLPolyR(project)
	}

	override fun isModified() = exePathField.text != settings.exePath

	override fun apply() {
		settings.exePath = exePathField.text
	}

	override fun getDisplayName() = TTBundle.message("mlpolyr.name")
}
