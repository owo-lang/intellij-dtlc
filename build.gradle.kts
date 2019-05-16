import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.*
import java.nio.file.Paths
import org.ice1000.tt.gradle.LanguageUtilityGenerationTask

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

val pluginComingVersion = "0.4.2"
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

	setPlugins("PsiViewer:191.4212")
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
		resources.srcDirs("res", "$buildDir/genRes")
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

fun grammar(name: String): Pair<GenerateParser, GenerateLexer> {
	val lowerCaseName = name.toLowerCase()
	val parserRoot = Paths.get("org", "ice1000", "tt", "psi", lowerCaseName)!!
	val lexerRoot = Paths.get("build", "gen", "org", "ice1000", "tt", "psi", lowerCaseName)!!
	fun path(more: Iterable<*>) = more.joinToString(File.separator)
	fun bnf(name: String) = Paths.get("grammar", "$name.bnf").toString()
	fun flex(name: String) = Paths.get("grammar", "$name.flex").toString()

	val genParser = task<GenerateParser>("gen${name}Parser") {
		group = "code generation"
		description = "Generate Parser and Psi classes for $name"
		source = bnf(lowerCaseName)
		targetRoot = "$buildDir/gen/"
		pathToParser = path(parserRoot + "${name}Parser.java")
		pathToPsiRoot = path(parserRoot)
		purgeOldFiles = true
	}

	return genParser to task<GenerateLexer>("gen${name}Lexer") {
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
// Obviously there's no Agda parser. But we can have an Agda lexer :).
val (genAgdaParser, genAgdaLexer) = grammar("Agda")

val genMiniTTUtility = task<LanguageUtilityGenerationTask>("genMiniTTUtility") {
	languageName = "MiniTT"
	constantPrefix = "MINI_TT"
	exeName = "minittc"
	runConfigInit = """additionalOptions = "--repl-plain""""
	trimVersion = """version.removePrefix("minittc").trim()"""
}

val genACoreUtility = task<LanguageUtilityGenerationTask>("genACoreUtility") {
	languageName = "ACore"
	constantPrefix = "AGDA_CORE"
	exeName = "agdacore"
	hasVersion = false
}

val genCubicalTTUtility = task<LanguageUtilityGenerationTask>("genCubicalTTUtility") {
	languageName = "CubicalTT"
	constantPrefix = "CUBICAL_TT"
	exeName = "cubical"
	trimVersion = "version.trim()"
}

val genAgdaUtility = task<LanguageUtilityGenerationTask>("genAgdaUtility") {
	languageName = "Agda"
	constantPrefix = "AGDA"
	exeName = "agda"
	trimVersion = """version.removePrefix("Agda version").trim()"""
}

val genMLPolyRUtility = task<LanguageUtilityGenerationTask>("genMLPolyRUtility") {
	languageName = "MLPolyR"
	constantPrefix = "MLPOLYR"
	exeName = "mlpolyrc"
	runConfigInit = """additionalOptions = "-t""""
	hasVersion = false
}

val genRedPrlUtility = task<LanguageUtilityGenerationTask>("genRedPrlUtility") {
	languageName = "RedPrl"
	constantPrefix = "RED_PRL"
	exeName = "redprl"
	hasVersion = false
}

val generateCode = task("generateCode") {
	group = "code generation"
}
generateCode.dependsOn(
	genMiniTTUtility,
	genMLPolyRUtility,
	genACoreUtility,
	genAgdaUtility,
	genRedPrlUtility,
	genCubicalTTUtility,
	genAgdaParser,
	genAgdaLexer,
	genACoreParser,
	genACoreLexer,
	genMLPolyRParser,
	genMLPolyRLexer,
	genRedPrlParser,
	genRedPrlLexer,
	genMiniTTParser,
	genMiniTTLexer
)

tasks.withType<KotlinCompile> {
	dependsOn(generateCode)
	kotlinOptions {
		jvmTarget = "1.8"
		languageVersion = "1.3"
		apiVersion = "1.3"
		freeCompilerArgs = listOf("-Xjvm-default=enable")
	}
}
