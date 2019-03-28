package org.ice1000.tt.execution

import com.intellij.execution.Executor
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiElement
import icons.TTIcons
import org.ice1000.tt.MINI_TT_RUN_CONFIG_ID
import org.ice1000.tt.TTBundle
import org.ice1000.tt.MiniTTFileType
import org.ice1000.tt.execution.ui.MiniTTRunConfigurationEditorImpl
import org.ice1000.tt.project.minittPath
import org.ice1000.tt.project.minittSettings
import org.ice1000.tt.validateMiniTTExe
import org.jdom.Element

class MiniTTRunConfiguration(
	project: Project,
	factory: ConfigurationFactory
) : LocatableConfigurationBase<MiniTTCommandLineState>(project, factory, TTBundle.message("minitt.name")) {
	var workingDir = project.guessProjectDir()?.path.orEmpty()
	var targetFile = ""
	var additionalOptions = "--repl-plain"
	var minittExecutable = project.minittSettings.settings.exePath

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
	override fun getIcon() = TTIcons.MINI_TT
	override fun getConfigurationTypeDescription() = TTBundle.message("minitt.run-config.description")
	override fun getId() = MINI_TT_RUN_CONFIG_ID
	override fun getDisplayName() = TTBundle.message("minitt.name")
	override fun getConfigurationFactories() = factories
}

@Suppress("DEPRECATION")
class MiniTTRunConfigurationProducer : RunConfigurationProducer<MiniTTRunConfiguration>(MiniTTRunConfigurationType) {
	override fun isConfigurationFromContext(
		configuration: MiniTTRunConfiguration, context: ConfigurationContext) =
		configuration.targetFile == context.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)?.path

	override fun setupConfigurationFromContext(
		configuration: MiniTTRunConfiguration, context: ConfigurationContext, ref: Ref<PsiElement>?): Boolean {
		val file = context.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)
		if (file?.fileType != MiniTTFileType) return false
		configuration.targetFile = file.path
		configuration.workingDir = context.project.basePath.orEmpty()
		configuration.name = FileUtilRt
			.getNameWithoutExtension(configuration.targetFile)
			.takeLastWhile { it != '/' && it != '\\' }
		val existPath = context.project
			.minittSettings
			.settings
			.exePath
		if (validateMiniTTExe(existPath)) configuration.minittExecutable = existPath
		else {
			val exePath = minittPath ?: return true
			if (validateMiniTTExe(exePath)) configuration.minittExecutable = exePath
		}
		return true
	}
}


