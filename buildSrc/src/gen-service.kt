package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language

fun LanguageUtilityGenerationTask.service(configName: String, nickname: String) {
	@Language("kotlin")
val service = """
@file:JvmMultifileClass
@file:JvmName("ProjectGenerated")
package $basePackage.project

import com.intellij.openapi.components.PersistentStateComponent
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
	get() = getComponent(${languageName}ProjectSettingsService::class.java)

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
"""
	val outProjectDir = outDir.resolve("project")
	outProjectDir.mkdirs()
	outProjectDir.resolve("$nickname-generated.kt").writeText(service)

	if (generateSettings) {
		val settingsClassName = "${languageName}ProjectConfigurable"
		@Language("JAVA")
		val settingsClassContent = if (hasVersion) """
package $basePackage.project;

import com.intellij.openapi.project.Project;
import org.ice1000.tt.TTBundle;
import org.ice1000.tt.project.ui.VersionedExecutableProjectConfigurableImpl;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.tt.project.ProjectGenerated.*;

class $settingsClassName extends VersionedExecutableProjectConfigurableImpl {
	private ${languageName}Settings settings;

	@NotNull @Override
	protected VersionedExecutableSettings getSettings() {
		return settings;
	}

	@NotNull @Override
	protected String trimVersion(@NotNull String version) {
		return $trimVersion;
	}

	public ${languageName}ProjectConfigurable(Project project) {
		settings = get${configName.capitalize()}Settings(project).getSettings();
		init();
		configure$languageName(this, project);
	}

	@Override
	public String getDisplayName() {
		return TTBundle.message("$nickname.name");
	}
}
""" else """
package $basePackage.project;

import com.intellij.openapi.project.Project;
import org.ice1000.tt.TTBundle;
import org.ice1000.tt.project.ui.OnlyExecutableProjectConfigurable;

import java.util.Objects;

import static org.ice1000.tt.project.ProjectGenerated.*;

class $settingsClassName extends OnlyExecutableProjectConfigurable {
	private ${languageName}Settings settings;

	public ${languageName}ProjectConfigurable(Project project) {
		settings = get${configName.capitalize()}Settings(project).getSettings();
		getExePathField().setText(settings.getExePath());
		configure$languageName(this, project);
	}

	@Override
	public boolean isModified() {
		return Objects.equals(getExePathField().getText(), settings.getExePath());
	}

	@Override
	public String getDisplayName() {
		return TTBundle.message("$nickname.name");
	}

	@Override
	public void apply() {
		settings.setExePath(getExePathField().getText());
	}
}
"""
		outProjectDir.resolve("$settingsClassName.java").writeText(settingsClassContent)
	}
}
