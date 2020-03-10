package org.ice1000.tt.gradle.json

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.stringify
import java.io.File

private val sharedJson = Json(JsonConfiguration.Stable)

const val DEFAULT_PKG = "org.ice1000.tt"

enum class FindUsagesProviderOpt {
	WithString,
	WithoutString,
	DontGenerate
}

@Serializable
class DeclType constructor(
	val name: String,
	val identifierName: String? = null,
	val icon: String = "super.getIcon(flags)",
	val findType: String? = null
)

@Serializable
class LangData constructor(
        val languageName: String,
        var constantPrefix: String,
        val exeName: String,
        val runConfigInit: String = "",
        val trimVersion: String = "version",
        val findUsagesProvider: FindUsagesProviderOpt = FindUsagesProviderOpt.DontGenerate,
        val generateCliState: Boolean = true,
        val generateRunConfig: Boolean = true,
        val website: String = "",
        val verification: Boolean = true,
        val hasVersion: Boolean = true,
        val generateSettings: Boolean = true,
        val generateService: Boolean = true,
        val supportsParsing: Boolean = false,
        val nameBasedReferenceTypes: List<String> = emptyList(),
        val declarationTypes: List<DeclType> = emptyList(),
        val declarationDefaultIdentifierName: String = "",
        val declarationDefaultFindType: String = "null",
        val keywordList: List<String> = emptyList(),
        val highlightTokenPairs: Map<String, String> = emptyMap(),
        val braceTokenPairs: Map<String, String> = emptyMap(),
        val basePackage: String = DEFAULT_PKG
) {
	fun toJson() = sharedJson.stringify(serializer(), this)

	@UseExperimental(ImplicitReflectionSerializer::class)
	companion object SchemaWriter {
		init {
			val map = schema(serializer().descriptor)
			File("build/fyi/schema.json").writeText(sharedJson.stringify(map))
		}
	}
}

fun langGenJson(json: File) = langGenJson(json.readText())
fun langGenJson(json: String) = sharedJson.parse(LangData.serializer(), json)
