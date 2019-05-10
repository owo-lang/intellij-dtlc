package org.ice1000.tt.project.ui

import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.labels.LinkLabel
import org.ice1000.tt.project.VersionedExecutableSettings
import org.ice1000.tt.versionOf
import javax.swing.JButton
import javax.swing.JPanel

abstract class CommonConfigurable : Configurable {
	lateinit var mainPanel: JPanel
	lateinit var exePathField: TextFieldWithBrowseButton
	lateinit var guessExeButton: JButton
	lateinit var websiteLabel: LinkLabel<*>

	override fun createComponent() = mainPanel
}

fun CommonConfigurable.initWebsiteLabel() = websiteLabel.setListener({ _, _ ->
	BrowserLauncher.instance.open(websiteLabel.text)
}, null)

abstract class VersionedExecutableProjectConfigurableImpl : VersionedExecutableProjectConfigurable() {
	protected abstract val settings: VersionedExecutableSettings
	protected abstract fun trimVersion(version: String): String

	init {
		exePathField.addPropertyChangeListener {
			versionLabel.text = trimVersion(versionOf(exePathField.text))
		}
	}

	override fun isModified() = exePathField.text != settings.exePath || versionLabel.text != settings.version
	override fun apply() {
		settings.exePath = exePathField.text
		settings.version = versionLabel.text
	}

	protected fun init() {
		exePathField.text = settings.exePath
		versionLabel.text = settings.version
	}
}
