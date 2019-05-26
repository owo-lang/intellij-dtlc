package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language

fun LanguageUtilityGenerationTask.service(configName: String, nickname: String) {
	@Language("kotlin")
val service = """
@file:JvmMultifileClass
@file:JvmName("ProjectGenerated")
package $basePackage.project

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.util.xmlb.XmlSerializerUtil
import icons.TTIcons
import org.ice1000.tt.${constantPrefix}_WEBSITE
import org.ice1000.tt.TTBundle
import org.ice1000.tt.project.ui.CommonConfigurable
import org.ice1000.tt.project.ui.initWebsiteLabel
import org.ice1000.tt.project.ui.OnlyExecutableProjectConfigurable
import org.ice1000.tt.project.ui.VersionedExecutableProjectConfigurableImpl

${if (generateSettings) if (hasVersion) """
data class ${languageName}Settings(
	override var exePath: String = "$exeName",
	override var version: String = "Unknown"
) : VersionedExecutableSettings
""" else """
data class ${languageName}Settings(var exePath: String = "$exeName")
""" else ""}
val ${configName}Path by lazyExePath("$exeName")

@State(
	name = "${languageName}ProjectSettings",
	storages = [Storage(file = "${languageName.decapitalize()}Config.xml")])
class ${languageName}ProjectSettingsService : PersistentStateComponent<${languageName}Settings> {
	val settings: ${languageName}Settings = ${languageName}Settings()
	override fun getState(): ${languageName}Settings? = XmlSerializerUtil.createCopy(settings)
	override fun loadState(state: ${languageName}Settings) {
		XmlSerializerUtil.copyBean(state, settings)
	}
}

val Project.${configName}Settings: ${languageName}ProjectSettingsService
	get() = ${configName}SettingsNullable ?: ${languageName}ProjectSettingsService()

/**
 * When building plugin searchable options,
 * `ServiceManager.getService(this, ${languageName}ProjectSettingsService::class.java)
 * may return null.
 */
val Project.${configName}SettingsNullable: ${languageName}ProjectSettingsService?
	get() = ServiceManager.getService(this, ${languageName}ProjectSettingsService::class.java)

internal fun CommonConfigurable.configure$languageName(project: Project) {
	initWebsiteLabel()
	websiteLabel.text = ${constantPrefix}_WEBSITE
	websiteLabel.icon = TTIcons.$constantPrefix
	exePathField.addBrowseFolderListener(TTBundle.message("$nickname.ui.project.select-compiler"),
		TTBundle.message("$nickname.ui.project.select-compiler.description"),
		project,
		FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor())
	guessExeButton.addActionListener {
		${configName}Path?.let { exePathField.text = it }
	}
}

${if (generateSettings) if (hasVersion) """
class ${languageName}ProjectConfigurable(project: Project) : VersionedExecutableProjectConfigurableImpl() {
	override val settings: ${languageName}Settings = project.${configName}Settings.settings

	init {
		init()
		configure$languageName(project)
	}

	override fun trimVersion(version: String) = $trimVersion
	override fun getDisplayName() = TTBundle.message("$nickname.name")
}
""" else """
class ${languageName}ProjectConfigurable(project: Project) : OnlyExecutableProjectConfigurable() {
	val settings: ${languageName}Settings = project.${configName}Settings.settings

	init {
		exePathField.text = settings.exePath
		configure$languageName(project)
	}

	override fun isModified() = exePathField.text != settings.exePath
	override fun getDisplayName() = TTBundle.message("$nickname.name")

	override fun apply() {
		settings.exePath = exePathField.text
	}
}
""" else ""}
"""
	outDir.resolve("project")
		.apply { mkdirs() }
		.resolve("$nickname-generated.kt").writeText(service)
}
