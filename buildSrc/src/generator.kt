package org.ice1000.tt.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.intellij.lang.annotations.Language

open class LanguageUtilityGenerationTask : DefaultTask() {
	@field:Input var basePackage: String = "org.ice1000.tt"
	@field:Input var languageName: String = ""
	@field:Input var constantPrefix: String = ""
	@field:Input var exeName: String = ""
	@field:Input var runConfigInit: String = ""
	@field:Input var trimVersion: String = "version"
	@field:Input var generateCliState: Boolean = true
	@field:Input var hasVersion: Boolean = true
	@field:Input var generateSettings: Boolean = true
	@field:OutputDirectory
	val outDir = basePackage.split('.').fold(project.projectDir.resolve("gen")) { dir, p -> dir.resolve(p) }

	init {
		group = "code generation"
	}

	@TaskAction
	fun gen() {
		val nickname = languageName.toLowerCase()
		val configName = when (languageName.length) {
			0, 1 -> nickname
			else -> languageName.substring(0, 2).toLowerCase() + languageName.substring(2)
		}
		outDir.mkdirs()
		if (languageName.isBlank()) throw GradleException("Language name of $name must not be empty.")
		if (exeName.isBlank()) throw GradleException("Executable name for $name must not be empty.")
		@Language("Java")
		val language = """
package $basePackage;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.tt.ConstantsKt.${constantPrefix}_LANGUAGE_NAME;

/**
 * @author ice1000
 */
public final class ${languageName}Language extends Language {
	public static final @NotNull ${languageName}Language INSTANCE =
			new ${languageName}Language(${constantPrefix}_LANGUAGE_NAME);

	private ${languageName}Language(@NotNull String name) {
		super(name, "text/" + ${constantPrefix}_LANGUAGE_NAME);
	}
}
"""
		outDir.resolve("${languageName}Language.java").writeText(language)
		@Language("kotlin")
		val infos = """
package org.ice1000.tt

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.psi.FileViewProvider
import icons.TTIcons

object ${languageName}FileType : LanguageFileType(${languageName}Language.INSTANCE) {
	override fun getDefaultExtension() = ${constantPrefix}_EXTENSION
	override fun getName() = TTBundle.message("$nickname.name")
	override fun getIcon() = TTIcons.${constantPrefix}_FILE
	override fun getDescription() = TTBundle.message("$nickname.name.description")
}

@Suppress("unused")
open class ${languageName}File(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ${languageName}Language.INSTANCE) {
	override fun getFileType() = ${languageName}FileType
}
"""
		outDir.resolve("$nickname-generated.kt").writeText(infos)
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
	@Language("kotlin")
		val fileCreation = """
package $basePackage.action

import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import icons.TTIcons
import org.ice1000.tt.TTBundle

object New${languageName}File : NewTTFile(
	TTBundle.message("$nickname.actions.new-file.name"),
	TTBundle.message("$nickname.actions.new-file.description"),
	TTIcons.${constantPrefix}_FILE) {
	override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
		builder
			.setTitle(TTBundle.message("$nickname.actions.new-file.title"))
			.addKind("File", TTIcons.${constantPrefix}_FILE, "$languageName File")
	}
}
"""
		outDir.resolve("action")
			.apply { mkdirs() }
			.resolve("$nickname-generated.kt").writeText(fileCreation)
		@Language("kotlin")
		val editing = """
package $basePackage.editing

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider
import com.intellij.psi.PsiFile
import org.ice1000.tt.*

class ${languageName}DefaultContext : TemplateContextType("${constantPrefix}_DEFAULT_CONTEXT_ID", ${constantPrefix}_LANGUAGE_NAME, TTParentContext::class.java) {
	override fun isInContext(file: PsiFile, offset: Int) = file.fileType == ${languageName}FileType
}
"""
		outDir.resolve("editing")
			.apply { mkdirs() }
			.resolve("$nickname-generated.kt").writeText(editing)
		@Language("kotlin")
		val runConfig = """
package $basePackage.execution

import com.intellij.execution.Executor
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiElement
import icons.TTIcons
import org.ice1000.tt.${languageName}FileType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.execution.ui.InterpretedRunConfigurationEditorImpl
import org.ice1000.tt.project.*
import org.ice1000.tt.validateExe
import org.jdom.Element

class ${languageName}RunConfigurationFactory(type: ${languageName}RunConfigurationType) : ConfigurationFactory(type) {
	override fun createTemplateConfiguration(project: Project) = ${languageName}RunConfiguration(project, this)
}

object ${languageName}RunConfigurationType : ConfigurationType {
	private val factories = arrayOf(${languageName}RunConfigurationFactory(this))
	override fun getIcon() = TTIcons.$constantPrefix
	override fun getConfigurationTypeDescription() = TTBundle.message("$nickname.run-config.description")
	override fun getId() = "${constantPrefix}_RUN_CONFIG_ID"
	override fun getDisplayName() = TTBundle.message("$nickname.name")
	override fun getConfigurationFactories() = factories
}

class ${languageName}RunConfiguration(
	project: Project,
	factory: ConfigurationFactory
) : InterpretedRunConfiguration<${languageName}CommandLineState>(project, factory, TTBundle.message("$nickname.name")) {
	var ${configName}Executable = project.${configName}Settings.settings.exePath
	init { $runConfigInit }

	override fun getState(executor: Executor, environment: ExecutionEnvironment) = ${languageName}CommandLineState(this, environment)
	override fun getConfigurationEditor() = ${languageName}RunConfigurationEditor(this, project)

	@Suppress("DEPRECATION")
	override fun readExternal(element: Element) {
		super.readExternal(element)
		JDOMExternalizerUtil.readField(element, "${configName}Executable").orEmpty().let { ${configName}Executable = it }
	}

	@Suppress("DEPRECATION")
	override fun writeExternal(element: Element) {
		super.writeExternal(element)
		JDOMExternalizerUtil.writeField(element, "${configName}Executable", ${configName}Executable)
	}
}

class ${languageName}RunConfigurationEditor(
	configuration: ${languageName}RunConfiguration,
	project: Project
) : InterpretedRunConfigurationEditorImpl<${languageName}RunConfiguration>(project) {
	init {
		targetFileField.addBrowseFolderListener(TTBundle.message("$nickname.ui.run-config.select-$nickname-file"),
			TTBundle.message("$nickname.ui.run-config.select-$nickname-file.description"),
			project,
			FileChooserDescriptorFactory.createSingleFileDescriptor(${languageName}FileType))
		exePathField.addBrowseFolderListener(TTBundle.message("$nickname.ui.project.select-compiler"),
			TTBundle.message("$nickname.ui.project.select-compiler.description"),
			project,
			FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor())
		resetEditorFrom(configuration)
	}

	override fun resetEditorFrom(s: ${languageName}RunConfiguration) {
		super.resetEditorFrom(s)
		exePathField.text = s.${configName}Executable
	}

	override fun applyEditorTo(s: ${languageName}RunConfiguration) {
		super.applyEditorTo(s)
		s.${configName}Executable = exePathField.text
	}
}

@Suppress("DEPRECATION")
class ${languageName}RunConfigurationProducer : RunConfigurationProducer<${languageName}RunConfiguration>(${languageName}RunConfigurationType) {
	override fun isConfigurationFromContext(
		configuration: ${languageName}RunConfiguration, context: ConfigurationContext) =
		configuration.targetFile == context.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)?.path

	override fun setupConfigurationFromContext(
		configuration: ${languageName}RunConfiguration, context: ConfigurationContext, ref: Ref<PsiElement>?): Boolean {
		val file = context.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)
		if (file?.fileType != ${languageName}FileType) return false
		val config = context.project.${configName}SettingsNullable ?: return false
		configuration.targetFile = file.path
		configuration.workingDir = context.project.basePath.orEmpty()
		configuration.name = FileUtilRt.getNameWithoutExtension(configuration.targetFile)
			.takeLastWhile { it != '/' && it != '\\' }
		val existPath = config.settings.exePath
		if (validateExe(existPath)) configuration.${configName}Executable = existPath
		else {
			val exePath = ${configName}Path ?: return true
			if (validateExe(exePath)) configuration.${configName}Executable = exePath
		}
		return true
	}
}
"""
		@Language("kotlin")
		val cliState = """
package $basePackage.execution

import com.intellij.execution.runners.ExecutionEnvironment

class ${languageName}CommandLineState(
	configuration: ${languageName}RunConfiguration,
	env: ExecutionEnvironment
) : InterpretedCliState<${languageName}RunConfiguration>(configuration, env) {
	override fun ${languageName}RunConfiguration.pre(params: MutableList<String>) {
		params += ${configName}Executable
	}
}
"""
		val exe = outDir.resolve("execution").apply { mkdirs() }
		exe.resolve("$nickname-generated.kt").writeText(runConfig)
		if (generateCliState) exe.resolve("$nickname-cli-state.kt").writeText(cliState)
	}
}
