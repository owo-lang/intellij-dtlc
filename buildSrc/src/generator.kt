package org.ice1000.tt.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.intellij.lang.annotations.Language

open class LanguageUtilityGenerationTask : DefaultTask() {
	@field:Input var basePackage: String = "org.ice1000.tt"
	@field:Input var languageName: String = ""
	@field:Input var constantPrefix: String = ""
	@field:Input var exeName: String = ""
	private val nickname get() = languageName.toLowerCase()
	private val configName get() = languageName.decapitalize()

	init {
		group = "code generation"
	}

	@TaskAction
	fun gen() {
		val dir = basePackage.split('.').fold(project.projectDir.resolve("gen")) { dir, p -> dir.resolve(p) }
		dir.mkdirs()
		if (languageName.isBlank()) throw GradleException("Language name of $name must not be empty.")
		if (exeName.isBlank()) throw GradleException("Executable name for $name must not be empty.")
		@Language("Java")
		val language = """
package org.ice1000.tt;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.tt.ConstantsKt.${constantPrefix}_LANGUAGE_NAME;

/**
 * @author ice1000
 */
public class ${languageName}Language extends Language {
	public static final @NotNull ${languageName}Language INSTANCE =
			new ${languageName}Language(${constantPrefix}_LANGUAGE_NAME);

	private ${languageName}Language(@NotNull String name) {
		super(name, "text/" + ${constantPrefix}_LANGUAGE_NAME);
	}
}
"""
		dir.resolve("${languageName}Language.java").writeText(language)
		@Language("kotlin")
		val service = """
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
import org.ice1000.tt.project.ui.VersionedExecutableProjectConfigurableImpl

data class ${languageName}Settings(
	override var exePath: String = "$exeName",
	override var version: String = "Unknown"
) : VersionedExecutableSettings

val ${nickname}Path by lazyExePath("$exeName")

interface ${languageName}ProjectSettingsService {
	val settings: ${languageName}Settings
}

@State(
	name = "${languageName}ProjectSettings",
	storages = [Storage(file = "${configName}Config.xml")])
class ${languageName}ProjectSettingsServiceImpl :
		${languageName}ProjectSettingsService, PersistentStateComponent<${languageName}Settings> {
	override val settings: ${languageName}Settings = ${languageName}Settings()
	override fun getState(): ${languageName}Settings? = XmlSerializerUtil.createCopy(settings)
	override fun loadState(state: ${languageName}Settings) {
		XmlSerializerUtil.copyBean(state, settings)
	}
}

val Project.${nickname}Settings: ${languageName}ProjectSettingsService
	get() = ${nickname}SettingsNullable!!

/** For building plugin searchable options */
val Project.${nickname}SettingsNullable: ${languageName}ProjectSettingsService?
	get() = ServiceManager.getService(this, ${languageName}ProjectSettingsService::class.java)


abstract class ${languageName}ProjectConfigurableBase(project: Project) : VersionedExecutableProjectConfigurableImpl() {
	/** For building searchable options */
	override val settings: ${languageName}Settings = project.${nickname}SettingsNullable?.settings ?: ${languageName}Settings()

	init {
		init()
		websiteLabel.text = ${constantPrefix}_WEBSITE
		websiteLabel.icon = TTIcons.$constantPrefix
		exePathField.addBrowseFolderListener(TTBundle.message("$nickname.ui.project.select-compiler"),
			TTBundle.message("$nickname.ui.project.select-compiler.description"),
			project,
			FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor())
		guessExeButton.addActionListener {
			${nickname}Path?.let { exePathField.text = it }
		}
	}

	override fun getDisplayName() = TTBundle.message("$nickname.name")
}
"""
		dir.resolve("project")
			.apply { mkdirs() }
			.resolve("$nickname-generated.kt").writeText(service)
	}
}
