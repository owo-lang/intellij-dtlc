package org.ice1000.minitt.execution

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizerUtil
import icons.MiniTTIcons
import org.ice1000.minitt.MINI_TT_RUN_CONFIG_ID
import org.ice1000.minitt.MiniTTBundle
import org.ice1000.minitt.execution.ui.MiniTTRunConfigurationEditorImpl
import org.jdom.Element

class MiniTTRunConfiguration(
	project: Project,
	factory: ConfigurationFactory
) : LocatableConfigurationBase<MiniTTCommandLineState>(project, factory, MiniTTBundle.message("minitt.name")) {
	var workingDir = ""
	var targetFile = ""
	var additionalOptions = "--repl"
	var minittExecutable = ""

	override fun getState(executor: Executor, environment: ExecutionEnvironment) = MiniTTCommandLineState(this, environment)
	override fun getConfigurationEditor() = MiniTTRunConfigurationEditorImpl(this, project)

	override fun readExternal(element: Element) {
		super.readExternal(element)
		JDOMExternalizerUtil.readField(element, "workingDir").orEmpty().let { workingDir = it }
		JDOMExternalizerUtil.readField(element, "targetFile").orEmpty().let { targetFile = it }
		JDOMExternalizerUtil.readField(element, "additionalOptions").orEmpty().let { additionalOptions = it }
		JDOMExternalizerUtil.readField(element, "minittExecutable").orEmpty().let { minittExecutable = it }
	}

	override fun writeExternal(element: Element) {
		super.writeExternal(element)
		JDOMExternalizerUtil.writeField(element, "workingDir", workingDir)
		JDOMExternalizerUtil.writeField(element, "targetFile", targetFile)
		JDOMExternalizerUtil.writeField(element, "additionalOptions", additionalOptions)
		JDOMExternalizerUtil.writeField(element, "minittExecutable", minittExecutable)
	}
}

class MiniTTRunConfigurationFactory(type: MiniTTRunConfigurationType) : ConfigurationFactory(type) {
	override fun createTemplateConfiguration(project: Project) = MiniTTRunConfiguration(project, this)
}

object MiniTTRunConfigurationType : ConfigurationType {
	private val factories = arrayOf(MiniTTRunConfigurationFactory(this))
	override fun getIcon() = MiniTTIcons.MINI_TT
	override fun getConfigurationTypeDescription() = MiniTTBundle.message("minitt.run-config.description")
	override fun getId() = MINI_TT_RUN_CONFIG_ID
	override fun getDisplayName() = MiniTTBundle.message("minitt.name")
	override fun getConfigurationFactories() = factories
}
