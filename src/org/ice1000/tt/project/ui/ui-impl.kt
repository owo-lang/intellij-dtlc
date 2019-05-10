package org.ice1000.tt.project.ui

import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.openapi.project.Project
import org.ice1000.tt.project.AgdaProjectConfigurableBase
import org.ice1000.tt.project.MiniTTProjectConfigurableBase
import org.ice1000.tt.project.VersionedExecutableSettings
import org.ice1000.tt.versionOf

abstract class VersionedExecutableProjectConfigurableImpl : VersionedExecutableProjectConfigurable() {
	protected abstract val settings: VersionedExecutableSettings
	protected abstract fun trimVersion(version: String): String

	private fun reinit() {
		versionLabel.text = trimVersion(versionOf(exePathField.text))
	}

	init {
		exePathField.addPropertyChangeListener { reinit() }
		websiteLabel.setListener({ _, _ -> BrowserLauncher.instance.open(websiteLabel.text) }, null)
	}

	override fun isModified() = exePathField.text != settings.exePath || versionLabel.text != settings.version
	override fun createComponent() = mainPanel
	override fun apply() {
		settings.exePath = exePathField.text
		settings.version = versionLabel.text
	}

	protected fun init() {
		exePathField.text = settings.exePath
		versionLabel.text = settings.version
	}
}

abstract class OnlyExecutableProjectConfigurableImpl : OnlyExecutableProjectConfigurable() {
	init {
		websiteLabel.setListener({ _, _ -> BrowserLauncher.instance.open(websiteLabel.text) }, null)
	}

	override fun createComponent() = mainPanel
}

class MiniTTProjectConfigurable(project: Project) : MiniTTProjectConfigurableBase(project) {
	override fun trimVersion(version: String) = version.removePrefix("minittc").trim()
}

class AgdaProjectConfigurable(project: Project) : AgdaProjectConfigurableBase(project) {
	override fun trimVersion(version: String) = version.removePrefix("Agda version").trim()
}
