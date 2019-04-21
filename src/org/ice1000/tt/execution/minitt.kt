package org.ice1000.tt.execution

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiElement
import org.ice1000.tt.MiniTTFileType
import org.ice1000.tt.project.minittPath
import org.ice1000.tt.project.minittSettings
import org.ice1000.tt.validateExe

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
		configuration.name = FileUtilRt.getNameWithoutExtension(configuration.targetFile)
			.takeLastWhile { it != '/' && it != '\\' }
		val existPath = context.project
			.minittSettings
			.settings
			.exePath
		if (validateExe(existPath)) configuration.minittExecutable = existPath
		else {
			val exePath = minittPath ?: return true
			if (validateExe(exePath)) configuration.minittExecutable = exePath
		}
		return true
	}
}
