package org.ice1000.tt.execution

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.JDOMExternalizerUtil
import org.jdom.Element

abstract class InterpretedRunConfiguration<T : RunProfileState>(
	project: Project,
	factory: ConfigurationFactory,
	name: String
) : LocatableConfigurationBase<T>(project, factory, name) {
	var workingDir = project.guessProjectDir()?.path.orEmpty()
	var targetFile = ""
	var additionalOptions = ""

	override fun readExternal(element: Element) {
		super.readExternal(element)
		JDOMExternalizerUtil.readField(element, "workingDir").orEmpty().let { workingDir = it }
		JDOMExternalizerUtil.readField(element, "targetFile").orEmpty().let { targetFile = it }
		JDOMExternalizerUtil.readField(element, "additionalOptions").orEmpty().let { additionalOptions = it }
	}

	override fun writeExternal(element: Element) {
		super.writeExternal(element)
		JDOMExternalizerUtil.writeField(element, "workingDir", workingDir)
		JDOMExternalizerUtil.writeField(element, "targetFile", targetFile)
		JDOMExternalizerUtil.writeField(element, "additionalOptions", additionalOptions)
	}
}
