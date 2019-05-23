package org.ice1000.tt.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

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
	@field:Input var supportsParsing: Boolean = false
	@field:Input var highlightTokenPairs: List<Pair<String, String>> = emptyList()
	@field:OutputDirectory
	val outDir = basePackage.split('.').fold(project.buildDir.resolve("gen")) { dir, p -> dir.resolve(p) }
	@field:OutputDirectory
	val pluginXmlDir = project.buildDir.resolve("genRes").resolve("META-INF")

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
		infos(nickname)
		service(configName, nickname)
		fileCreation(nickname)
		editing(nickname)
		execution(nickname, configName)
		pluginXml(nickname)
		if (supportsParsing) {
			parser(configName, nickname)
			lexHighlight(configName, nickname)
		}
	}
}
