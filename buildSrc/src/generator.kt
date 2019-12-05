package org.ice1000.tt.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.ice1000.tt.gradle.json.DEFAULT_PKG
import org.ice1000.tt.gradle.json.LangData
import org.ice1000.tt.gradle.json.langGenJson
import java.io.File

val Task.defaultPkg
	get() = DEFAULT_PKG.split('.').fold(project.buildDir.resolve("gen")) { dir, p -> dir.resolve(p) }

open class GenMiscTask : DefaultTask() {
	@field:InputDirectory val input: String = "lang"
	@field:OutputDirectory val outDir = defaultPkg

	init {
		group = "code generation"
	}

	@TaskAction
	fun gen() {
		fileCreationGroup(langGenTasks, outDir)
		fileTypeFactory(langGenTasks, outDir)
	}

	companion object LangGenTaskHolder {
		@get:Synchronized val langGenTasks = mutableListOf<LangData>()
	}
}

open class LangUtilGenTask : DefaultTask() {
	@field:InputFile var langDataPath: String = ""
	@field:OutputDirectory val outDir = defaultPkg
	@field:OutputDirectory
	val fyiDir = project.buildDir.resolve("fyi")

	init {
		group = "code generation"
	}

	@TaskAction
	fun gen() {
		val langDataFile = File(langDataPath)
		if (!langDataFile.exists()) throw GradleException("File $langDataFile does not exists!")
		langGenJson(langDataFile).also { GenMiscTask.langGenTasks.add(it) }.doGen()
	}

	private fun LangData.doGen() {
		val nickname = languageName.toLowerCase()
		val configName = when (languageName.length) {
			0, 1 -> nickname
			else -> languageName.substring(0, 2).toLowerCase() + languageName.substring(2)
		}
		outDir.mkdirs()
		if (languageName.isBlank()) throw GradleException("Language name of $name must not be empty.")
		if (generateSettings && exeName.isBlank()) throw GradleException("Executable name for $name must not be empty.")
		if (constantPrefix.isEmpty()) constantPrefix = languageName.toUpperCase()
		infos(nickname, outDir)
		if (generateService) service(configName, nickname, outDir)
		fileCreation(outDir)
		editing(outDir)
		if (generateRunConfig) execution(nickname, configName, outDir)
		pluginXml(nickname, fyiDir)
		if (supportsParsing) {
			declarationDefaultName(nickname, outDir)
			declarationMixins(nickname, outDir)
			referenceMixins(nickname, outDir)
			parser(nickname, outDir)
			lexHighlight(nickname, outDir)
		}
		if (keywordList.isNotEmpty()) {
			completionContributor(nickname, outDir)
		}
		if (braceTokenPairs.isNotEmpty()) braceMatcher(nickname, outDir)
		findUsages(nickname, outDir)
	}
}
