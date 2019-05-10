package org.ice1000.tt.project

import com.intellij.openapi.project.Project
import org.ice1000.tt.TTBundle
import org.ice1000.tt.project.ui.OnlyExecutableProjectConfigurableImpl

data class ACoreSettings(
	var exePath: String = "agdacore"
)

class ACoreProjectConfigurable(project: Project) : OnlyExecutableProjectConfigurableImpl() {
	private val settings = project.acoreSettingsNullable?.settings ?: ACoreSettings()

	init {
		exePathField.text = settings.exePath
		configureACore(project)
	}

	override fun isModified() = exePathField.text != settings.exePath

	override fun apply() {
		settings.exePath = exePathField.text
	}

	override fun getDisplayName() = TTBundle.message("acore.name")
}
