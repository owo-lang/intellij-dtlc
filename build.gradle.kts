import org.ice1000.tt.gradle.LanguageUtilityGenerationTask
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.*
import java.nio.file.Paths

val isCI = !System.getenv("CI").isNullOrBlank()
val commitHash = kotlin.run {
	val process: Process = Runtime.getRuntime().exec("git rev-parse --short HEAD")
	process.waitFor()
	@Suppress("RemoveExplicitTypeArguments")
	val output = process.inputStream.use {
		process.inputStream.use {
			it.readBytes().let<ByteArray, String>(::String)
		}
	}
	process.destroy()
	output.trim()
}

val pluginComingVersion = "0.5.0"
val pluginVersion = if (isCI) "$pluginComingVersion-$commitHash" else pluginComingVersion
val packageName = "org.ice1000.tt"

group = packageName
version = pluginVersion

plugins {
	java
	id("org.jetbrains.intellij") version "0.4.8"
	id("org.jetbrains.grammarkit") version "2019.1"
	kotlin("jvm") version "1.3.30"
}

fun fromToolbox(root: String, ide: String) = file(root)
	.resolve(ide)
	.takeIf { it.exists() }
	?.resolve("ch-0")
	?.listFiles()
	.orEmpty()
	.filterNotNull()
	.filter { it.isDirectory }
	.maxBy {
		val (major, minor, patch) = it.name.split('.')
		String.format("%5s%5s%5s", major, minor, patch)
	}
	?.also { println("Picked: $it") }

allprojects { apply { plugin("org.jetbrains.grammarkit") } }

intellij {
	updateSinceUntilBuild = false
	instrumentCode = true
	val user = System.getProperty("user.name")
	val os = System.getProperty("os.name")
	val root = when {
		os.startsWith("Windows") -> "C:\\Users\\$user\\AppData\\Local\\JetBrains\\Toolbox\\apps"
		os == "Linux" -> "/home/$user/.local/share/JetBrains/Toolbox/apps"
		else -> return@intellij
	}
	val intellijPath = sequenceOf("IDEA-C-JDK11", "IDEA-C", "IDEA-JDK11", "IDEA-U")
		.mapNotNull { fromToolbox(root, it) }.firstOrNull()
	intellijPath?.absolutePath?.let { localPath = it }
	val pycharmPath = sequenceOf("PyCharm-C", "IDEA-C-JDK11", "IDEA-C", "IDEA-JDK11", "IDEA-U")
		.mapNotNull { fromToolbox(root, it) }.firstOrNull()
	pycharmPath?.absolutePath?.let { alternativeIdePath = it }

	setPlugins("PsiViewer:192-SNAPSHOT")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<PatchPluginXmlTask> {
	changeNotes(file("res/META-INF/change-notes.html").readText())
	pluginDescription(file("res/META-INF/description.html").readText())
	version(pluginVersion)
	pluginId(packageName)
}

sourceSets {
	main {
		withConvention(KotlinSourceSet::class) {
			listOf(java, kotlin).forEach { it.srcDirs("src", "$buildDir/gen") }
		}
		resources.srcDir("res")
	}

	test {
		withConvention(KotlinSourceSet::class) {
			listOf(java, kotlin).forEach { it.srcDirs("test") }
		}
		resources.srcDir("testData")
	}
}

repositories { mavenCentral() }

dependencies {
	compile(kotlin("stdlib-jdk8"))
	compile("org.eclipse.mylyn.github", "org.eclipse.egit.github.core", "2.1.5") {
		exclude(module = "gson")
	}
	testCompile(kotlin("test-junit"))
	testCompile("junit", "junit", "4.12")
}

task("displayCommitHash") {
	group = "help"
	description = "Display the newest commit hash"
	doFirst { println("Commit hash: $commitHash") }
}

task("isCI") {
	group = "help"
	description = "Check if it's running in a continuous-integration"
	doFirst { println(if (isCI) "Yes, I'm on a CI." else "No, I'm not on CI.") }
}

val generateCode = task("generateCode") {
	group = "code generation"
}

fun grammar(name: String): Pair<GenerateParser, GenerateLexer> {
	val lowerCaseName = name.toLowerCase()
	val parserRoot = Paths.get("org", "ice1000", "tt", "psi", lowerCaseName)!!
	val lexerRoot = Paths.get("build", "gen", "org", "ice1000", "tt", "psi", lowerCaseName)!!
	fun path(more: Iterable<*>) = more.joinToString(File.separator)
	fun bnf(name: String) = Paths.get("grammar", "$name.bnf").toString()
	fun flex(name: String) = Paths.get("grammar", "$name.flex").toString()

	val genParser = task<GenerateParser>("gen${name}Parser") {
		generateCode.dependsOn(this)
		group = generateCode.group
		description = "Generate Parser and Psi classes for $name"
		source = bnf(lowerCaseName)
		targetRoot = "$buildDir/gen/"
		pathToParser = path(parserRoot + "${name}Parser.java")
		pathToPsiRoot = path(parserRoot)
		purgeOldFiles = true
	}

	return genParser to task<GenerateLexer>("gen${name}Lexer") {
		generateCode.dependsOn(this)
		group = genParser.group
		description = "Generate Lexer for $name"
		source = flex(lowerCaseName)
		targetDir = path(lexerRoot)
		targetClass = "${name}Lexer"
		purgeOldFiles = true
		dependsOn(genParser)
	}
}

val (genMiniTTParser, genMiniTTLexer) = grammar("MiniTT")
val (genACoreParser, genACoreLexer) = grammar("ACore")
val (genMLPolyRParser, genMLPolyRLexer) = grammar("MLPolyR")
val (genRedPrlParser, genRedPrlLexer) = grammar("RedPrl")
val (genAgdaParser, genAgdaLexer) = grammar("Agda")
val (genCubicalTTParser, genCubicalTTLexer) = grammar("CubicalTT")

fun utilities(name: String, job: LanguageUtilityGenerationTask.() -> Unit) = task<LanguageUtilityGenerationTask>(name) {
	this.job()
	generateCode.dependsOn(this)
}

// Useful regexs:
// \@JvmField val ([A-Z_]+) = TextAttributesKey.createTextAttributesKey\("[^\"]+", DefaultLanguageHighlighterColors.([A-Z_]+)\)

val genMiniTTUtility = utilities("genMiniTTUtility") {
	languageName = "MiniTT"
	constantPrefix = "MINI_TT"
	exeName = "minittc"
	runConfigInit = """additionalOptions = "--repl-plain""""
	trimVersion = """version.removePrefix("minittc").trim()"""
	supportsParsing = true
	highlightTokenPairs = listOf(
		"KEYWORD" to "KEYWORD",
		"IDENTIFIER" to "IDENTIFIER",
		"FUNCTION_NAME" to "FUNCTION_DECLARATION",
		"CONSTRUCTOR_CALL" to "FUNCTION_CALL",
		"CONSTRUCTOR_DECL" to "FUNCTION_DECLARATION",
		"SEMICOLON" to "SEMICOLON",
		"COMMA" to "COMMA",
		"UNRESOLVED" to "IDENTIFIER",
		"OPERATOR" to "OPERATION_SIGN",
		"PAREN" to "PARENTHESES",
		"BRACE" to "BRACES",
		"COMMENT" to "LINE_COMMENT")
}

val genACoreUtility = utilities("genACoreUtility") {
	languageName = "ACore"
	constantPrefix = "AGDA_CORE"
	exeName = "agdacore"
	hasVersion = false
	supportsParsing = true
	highlightTokenPairs = listOf(
		"KEYWORD" to "KEYWORD",
		"IDENTIFIER" to "IDENTIFIER",
		"FUNCTION_NAME" to "FUNCTION_DECLARATION",
		"SEMICOLON" to "SEMICOLON",
		"COMMA" to "COMMA",
		"UNRESOLVED" to "IDENTIFIER",
		"OPERATOR" to "OPERATION_SIGN",
		"PAREN" to "PARENTHESES",
		"LINE_COMMENT" to "LINE_COMMENT",
		"BLOCK_COMMENT" to "BLOCK_COMMENT")
}

val genVoileUtility = utilities("genVoileUtility") {
	languageName = "Voile"
	constantPrefix = "VOILE"
	exeName = "voilec"
	trimVersion = """version.removePrefix("voilec").trim()"""
}

val genCubicalTTUtility = utilities("genCubicalTTUtility") {
	languageName = "CubicalTT"
	constantPrefix = "CUBICAL_TT"
	exeName = "cubical"
	trimVersion = "version.trim()"
	supportsParsing = true
	highlightTokenPairs = listOf(
		"KEYWORD" to "KEYWORD",
		"IDENTIFIER" to "IDENTIFIER",
		"SEMICOLON" to "SEMICOLON",
		"FUNCTION_NAME" to "FUNCTION_DECLARATION",
		"DATATYPE_NAME" to "CLASS_NAME",
		"COMMA" to "COMMA",
		"PAREN" to "PARENTHESES",
		"BRACK" to "BRACKETS",
		"UNDEFINED" to "KEYWORD",
		"HOLE" to "LABEL",
		"DIMENSION" to "NUMBER",
		"PROJECTION" to "INSTANCE_FIELD",
		"LINE_COMMENT" to "LINE_COMMENT",
		"BLOCK_COMMENT" to "BLOCK_COMMENT")
}

val genAgdaUtility = utilities("genAgdaUtility") {
	languageName = "Agda"
	constantPrefix = "AGDA"
	exeName = "agda"
	trimVersion = """version.removePrefix("Agda version").trim()"""
	supportsParsing = true
	highlightTokenPairs = listOf(
		"KEYWORD" to "KEYWORD",
		"IDENTIFIER" to "IDENTIFIER",
		"FUNCTION_NAME" to "FUNCTION_DECLARATION",
		"SEMICOLON" to "SEMICOLON",
		"DOT" to "DOT",
		"LINE_COMMENT" to "LINE_COMMENT",
		"BLOCK_COMMENT" to "BLOCK_COMMENT",
		"NUMBER" to "NUMBER",
		"STR_LIT" to "STRING",
		"CHR_LIT" to "STRING",
		"FLOAT" to "NUMBER",
		"ARROW" to "OPERATION_SIGN",
		"HOLE" to "LABEL",
		"PAREN" to "PARENTHESES",
		"BRACK" to "BRACKETS",
		"BRACE" to "BRACES",
		"PRAGMA" to "METADATA")
}

val genMLPolyRUtility = utilities("genMLPolyRUtility") {
	languageName = "MLPolyR"
	constantPrefix = "MLPOLYR"
	exeName = "mlpolyrc"
	runConfigInit = """additionalOptions = "-t""""
	hasVersion = false
	supportsParsing = true
	highlightTokenPairs = listOf(
		"KEYWORD" to "KEYWORD",
		"IDENTIFIER" to "IDENTIFIER",
		"UNRESOLVED" to "IDENTIFIER",
		"SEMICOLON" to "SEMICOLON",
		"COMMA" to "COMMA",
		"OPERATOR" to "OPERATION_SIGN",
		"PAREN" to "PARENTHESES",
		"BRACK" to "BRACKETS",
		"BRACE" to "BRACES",
		"BRACE2" to "BRACES",
		"COMMENT" to "BLOCK_COMMENT",
		"DOT" to "DOT",
		"INT" to "NUMBER",
		"STRING" to "STRING",

		"FUNCTION_CALL" to "FUNCTION_CALL",
		"FUNCTION_DECL" to "FUNCTION_DECLARATION",
		"VARIABLE_CALL" to "GLOBAL_VARIABLE",
		"VARIABLE_DECL" to "GLOBAL_VARIABLE",
		"PATTERN_CALL" to "LOCAL_VARIABLE",
		"PATTERN_DECL" to "LOCAL_VARIABLE",
		"FIELD_CALL" to "INSTANCE_FIELD",
		"FIELD_DECL" to "INSTANCE_FIELD",
		"PARAMETER_CALL" to "PARAMETER",
		"PARAMETER_DECL" to "PARAMETER",
		"CONSTRUCTOR" to "LABEL")
}

val genRedPrlUtility = utilities("genRedPrlUtility") {
	languageName = "RedPrl"
	constantPrefix = "RED_PRL"
	exeName = "redprl"
	hasVersion = false
	supportsParsing = true
	highlightTokenPairs = listOf(
		"PAREN" to "PARENTHESES",
		"BRACK" to "BRACKETS",
		"BRACE" to "BRACES",
		"KEYWORD" to "KEYWORD",
		"OP_NAME_DECL" to "GLOBAL_VARIABLE",
		"OP_NAME_CALL" to "GLOBAL_VARIABLE",
		"VAR_NAME_DECL" to "LOCAL_VARIABLE",
		"VAR_NAME_CALL" to "LOCAL_VARIABLE",
		"SEMICOLON" to "SEMICOLON",
		"COMMA" to "COMMA",
		"DOT" to "DOT",
		"OPERATOR" to "OPERATION_SIGN",
		"LINE_COMMENT" to "LINE_COMMENT",
		"BLOCK_COMMENT" to "BLOCK_COMMENT",
		"NUMERAL" to "NUMBER",
		"HASH" to "METADATA",
		"META_VAR_DECL" to "METADATA",
		"META_VAR_CALL" to "METADATA",
		"HOLE" to "LABEL")
}

tasks.withType<KotlinCompile> {
	dependsOn(generateCode)
	kotlinOptions {
		jvmTarget = "1.8"
		languageVersion = "1.3"
		apiVersion = "1.3"
		freeCompilerArgs = listOf("-Xjvm-default=enable")
	}
}
