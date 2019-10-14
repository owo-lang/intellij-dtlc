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

val Project.${configName}Settings: ${languageName}ProjectSettingsService
	get() = ${configName}SettingsNullable ?: ${languageName}ProjectSettingsService()

/**
 * When building plugin searchable options,
 * `ServiceManager.getService(this, ${languageName}ProjectSettingsService::class.java)
 * may return null.
 */
val Project.${configName}SettingsNullable: ${languageName}ProjectSettingsService?
	get() = getComponent(${languageName}ProjectSettingsService::class.java)
"""
	val outProjectDir = outDir.resolve("project")
	outProjectDir.mkdirs()
	outProjectDir.resolve("$nickname-generated.kt").writeText(service)

	val serviceClassName = "${languageName}ProjectSettingsService"
	@Language("JAVA")
	val serviceClassContent = """
package org.ice1000.tt.project;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
	name = "${languageName}ProjectSettings",
	storages = @Storage("${languageName.decapitalize()}Config.xml"))
public final class $serviceClassName implements PersistentStateComponent<${languageName}Settings> {
	private @NotNull ${languageName}Settings settings = new ${languageName}Settings();
	@NotNull public ${languageName}Settings getSettings() { return settings; }

	@Nullable @Override
	public ${languageName}Settings getState() { return XmlSerializerUtil.createCopy(settings); }

	@Override
	public void loadState(@NotNull ${languageName}Settings state) {
		XmlSerializerUtil.copyBean(state, settings);
	}
}"""
	outProjectDir.resolve("$serviceClassName.java").writeText(serviceClassContent)

	if (generateSettings) {
		val settingsClassName = "${languageName}ProjectConfigurable"
		val capitalizedConfigName = configName.capitalize()

		val configureSettings = """
initWebsiteLabel(this);
getWebsiteLabel().setText(${constantPrefix}_WEBSITE);
getWebsiteLabel().setIcon(TTIcons.$constantPrefix);
getExePathField().addBrowseFolderListener(TTBundle.message("$nickname.ui.project.select-compiler"),
	TTBundle.message("$nickname.ui.project.select-compiler.description"),
	project,
	FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
getGuessExeButton().addActionListener(e -> {
	String path = get${capitalizedConfigName}Path();
	if (path != null) getExePathField().setText(path);
});
"""

		@Language("JAVA")
		val settingsClassContent = if (hasVersion) """
package $basePackage.project;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import icons.TTIcons;
import org.ice1000.tt.TTBundle;
import org.ice1000.tt.project.ui.VersionedExecutableProjectConfigurableImpl;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.tt.ConstantsKt.${constantPrefix}_WEBSITE;
import static org.ice1000.tt.project.ProjectGenerated.*;
import static org.ice1000.tt.project.ui.Ui_implKt.initWebsiteLabel;

public final class $settingsClassName extends VersionedExecutableProjectConfigurableImpl {
	private ${languageName}Settings settings;

	@NotNull @Override
	protected VersionedExecutableSettings getSettings() {
		return settings;
	}

	@NotNull @Override
	protected String trimVersion(@NotNull String version) { return $trimVersion; }

	public $settingsClassName(Project project) {
		settings = get${capitalizedConfigName}Settings(project).getSettings();
		init();
		$configureSettings
	}

	@Override
	public String getDisplayName() { return TTBundle.message("$nickname.name"); }
}
""" else """
package $basePackage.project;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import icons.TTIcons;
import org.ice1000.tt.TTBundle;
import org.ice1000.tt.project.ui.OnlyExecutableProjectConfigurable;

import java.util.Objects;

import static org.ice1000.tt.ConstantsKt.${constantPrefix}_WEBSITE;
import static org.ice1000.tt.project.ProjectGenerated.*;
import static org.ice1000.tt.project.ui.Ui_implKt.initWebsiteLabel;

public final class $settingsClassName extends OnlyExecutableProjectConfigurable {
	private ${languageName}Settings settings;

	public $settingsClassName(Project project) {
		settings = get${capitalizedConfigName}Settings(project).getSettings();
		getExePathField().setText(settings.getExePath());
		$configureSettings
	}

	@Override
	public boolean isModified() {
		return Objects.equals(getExePathField().getText(), settings.getExePath());
	}

	@Override
	public String getDisplayName() { return TTBundle.message("$nickname.name"); }

	@Override
	public void apply() { settings.setExePath(getExePathField().getText()); }
}
"""
		outProjectDir.resolve("$settingsClassName.java").writeText(settingsClassContent)
	}
}
