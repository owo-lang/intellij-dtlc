package org.ice1000.tt.project.ui

import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.labels.LinkLabel
import org.ice1000.tt.project.AgdaProjectConfigurableBase
import org.ice1000.tt.project.MiniTTProjectConfigurableBase
import org.ice1000.tt.project.VersionedExecutableSettings
import org.ice1000.tt.versionOf
import javax.swing.JButton
import javax.swing.JPanel

interface CommonConfigurable : Configurable {
	val mainPanel: JPanel
	val exePathField: TextFieldWithBrowseButton
	val guessExeButton: JButton
	val websiteLabel: LinkLabel<*>

	override fun createComponent() = mainPanel
}

abstract class VersionedExecutableProjectConfigurableImpl : VersionedExecutableProjectConfigurable() {
	protected abstract val settings: VersionedExecutableSettings
	protected abstract fun trimVersion(version: String): String

	init {
		exePathField.addPropertyChangeListener {
			versionLabel.text = trimVersion(versionOf(exePathField.text))
		}
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
