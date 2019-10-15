package org.ice1000.tt.gradle

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File

private val sharedJson = Json(JsonConfiguration.Stable)

const val DEFAULT_PKG = "org.ice1000.tt"

@Serializable
class LangData(
	var languageName: String,
	var constantPrefix: String,
	var exeName: String,
	var runConfigInit: String = "",
	var trimVersion: String = "version",
	var generateCliState: Boolean = true,
	var hasVersion: Boolean = true,
	var generateSettings: Boolean = true,
	var supportsParsing: Boolean = false,
	var highlightTokenPairs: Map<String, String> = emptyMap(),
	var basePackage: String = DEFAULT_PKG
) {
	fun toJson() = sharedJson.stringify(serializer(), this)
}

fun langGenJson(json: File) = langGenJson(json.readText())
fun langGenJson(json: String) = sharedJson.parse(LangData.serializer(), json)
