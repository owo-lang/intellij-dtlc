package org.ice1000.tt.gradle

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File
import kotlin.reflect.KMutableProperty

private val sharedJson = Json(JsonConfiguration.Stable)

@Serializable
class LangData(
	var languageName: String = "",
	var constantPrefix: String = "",
	var exeName: String = "",
	var runConfigInit: String = "",
	var trimVersion: String = "",
	var generateCliState: Boolean = true,
	var hasVersion: Boolean = true,
	var generateSettings: Boolean = true,
	var supportsParsing: Boolean = false,
	var highlightTokenPairs: Map<String, String> = emptyMap(),
	var basePackage: String = "org.ice1000.tt"
) {
	fun toTask(out: LangUtilGenTask) {
		out.highlightTokenPairs = highlightTokenPairs.toList()
		val outClassProperties = out::class.members.filterIsInstance<KMutableProperty<*>>()
		this::class.members
			.filterIsInstance<KMutableProperty<*>>()
			.filter { it.name != ::highlightTokenPairs.name }
			.forEach { from ->
				val found = outClassProperties.find { it.name == from.name } ?: return@forEach
				found.setter.call(out, from.getter.call(this))
			}
	}

	fun fromTask(input: LangUtilGenTask) {
		highlightTokenPairs = input.highlightTokenPairs.toMap()
		val inputClassProperties = input::class.members.filterIsInstance<KMutableProperty<*>>()
		this::class.members
			.filterIsInstance<KMutableProperty<*>>()
			.filter { it.name != ::highlightTokenPairs.name }
			.forEach { from ->
				val found = inputClassProperties.find { it.name == from.name } ?: return@forEach
				from.setter.call(this, found.getter.call(input))
			}
	}

	fun toJson() = sharedJson.stringify(serializer(), this)
}

fun LangUtilGenTask.langGenJson(json: File) = langGenJson(json.readText())

fun LangUtilGenTask.langGenJson(json: String) {
}
