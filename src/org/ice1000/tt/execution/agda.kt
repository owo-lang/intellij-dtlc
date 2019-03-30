package org.ice1000.tt.execution

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizerUtil
import icons.TTIcons
import org.ice1000.tt.AGDA_RUN_CONFIG_ID
import org.ice1000.tt.AgdaFileType
import org.ice1000.tt.TTBundle
import org.ice1000.tt.execution.ui.InterpretedRunConfigurationEditorImpl
import org.ice1000.tt.project.agdaSettings
import org.jdom.Element

class AgdaCommandLineState(
	configuration: AgdaRunConfiguration,
	env: ExecutionEnvironment
) : InterpretedCliState<AgdaRunConfiguration>(configuration, env) {
	override fun AgdaRunConfiguration.pre(params: MutableList<String>) {
		params += agdaExecutable
	}
}

class AgdaRunConfiguration(
	project: Project,
	factory: ConfigurationFactory
) : InterpretedRunConfiguration<AgdaCommandLineState>(project, factory, TTBundle.message("agda.name")) {
	var agdaExecutable = project.agdaSettings.settings.exePath

	override fun getState(executor: Executor, environment: ExecutionEnvironment) = AgdaCommandLineState(this, environment)
	override fun getConfigurationEditor() = AgdaRunConfigurationEditor(this, project)

	override fun readExternal(element: Element) {
		super.readExternal(element)
		JDOMExternalizerUtil.readField(element, "agdaExecutable").orEmpty().let { agdaExecutable = it }
	}

	override fun writeExternal(element: Element) {
		super.writeExternal(element)
		JDOMExternalizerUtil.writeField(element, "agdaExecutable", agdaExecutable)
	}
}

class AgdaRunConfigurationFactory(type: AgdaRunConfigurationType) : ConfigurationFactory(type) {
	override fun createTemplateConfiguration(project: Project) = AgdaRunConfiguration(project, this)
}

object AgdaRunConfigurationType : ConfigurationType {
	private val factories = arrayOf(AgdaRunConfigurationFactory(this))
	override fun getIcon() = TTIcons.AGDA
	override fun getConfigurationTypeDescription() = TTBundle.message("agda.run-config.description")
	override fun getId() = AGDA_RUN_CONFIG_ID
	override fun getDisplayName() = TTBundle.message("agda.name")
	override fun getConfigurationFactories() = factories
}

class AgdaRunConfigurationEditor(
	configuration: AgdaRunConfiguration,
	project: Project
) : InterpretedRunConfigurationEditorImpl<AgdaRunConfiguration>(project) {
	init {
		targetFileField.addBrowseFolderListener(TTBundle.message("agda.ui.run-config.select-agda-file"),
			TTBundle.message("agda.ui.run-config.select-agda-file.description"),
			project,
			FileChooserDescriptorFactory.createSingleFileDescriptor(AgdaFileType))
		exePathField.addBrowseFolderListener(TTBundle.message("agda.ui.project.select-compiler"),
			TTBundle.message("agda.ui.project.select-compiler.description"),
			project,
			FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor())
		resetEditorFrom(configuration)
	}

	override fun resetEditorFrom(s: AgdaRunConfiguration) {
		super.resetEditorFrom(s)
		exePathField.text = s.agdaExecutable
	}

	override fun applyEditorTo(s: AgdaRunConfiguration) {
		super.applyEditorTo(s)
		s.agdaExecutable = exePathField.text
	}
}