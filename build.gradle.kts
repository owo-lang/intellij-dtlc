import org.ice1000.tt.gradle.GenMiscTask
import org.ice1000.tt.gradle.LangUtilGenTask
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val isCI = !System.getenv("CI").isNullOrBlank()
val commitHash = kotlin.run {
	val process: Process = Runtime.getRuntime().exec("git rev-parse --short HEAD")
	process.waitFor()
	val output = process.inputStream.use {
		process.inputStream.use { it.readBytes().let(::String) }
	}
	process.destroy()
	output.trim()
}

val pluginComingVersion = "0.9.2"
val pluginVersion = if (isCI) "$pluginComingVersion-$commitHash" else pluginComingVersion
val packageName = "org.ice1000.tt"

group = packageName
version = pluginVersion

plugins {
	java
	id("org.jetbrains.intellij") version "0.4.14"
	id("org.jetbrains.grammarkit") version "2019.3"
	kotlin("jvm") version "1.3.60"
}

grammarKit {
	grammarKitRelease = "7aecfcd72619e9c241866578e8312f339b4ddbd8"
}

fun fromToolbox(root: String, ide: String) = file(root)
	.resolve(ide)
	.takeIf { it.exists() }
	?.resolve("ch-0")
	?.listFiles()
	.orEmpty()
	.asSequence()
	.filterNotNull()
	.filter { it.isDirectory }
	.filterNot { it.name.endsWith(".plugins") }
	.maxBy {
		val (major, minor, patch) = it.name.split('.')
		String.format("%5s%5s%5s", major, minor, patch)
	}
	?.also { println("Picked: $it") }

allprojects { apply { plugin("org.jetbrains.grammarkit") } }

intellij {
	updateSinceUntilBuild = false
	instrumentCode = true
	// downloadSources = true
	val user = System.getProperty("user.name")
	val os = System.getProperty("os.name")
	val root = when {
		os.startsWith("Windows") -> "C:\\Users\\$user\\AppData\\Local\\JetBrains\\Toolbox\\apps"
		os == "Linux" -> "/home/$user/.local/share/JetBrains/Toolbox/apps"
		else -> return@intellij
	}
	val intellijPath = sequenceOf("IDEA-C", "IDEA-U")
		.mapNotNull { fromToolbox(root, it) }.firstOrNull()
	intellijPath?.absolutePath?.let { localPath = it }
	val pycharmPath = sequenceOf("PyCharm-C", "IDEA-C", "IDEA-U")
		.mapNotNull { fromToolbox(root, it) }.firstOrNull()
	pycharmPath?.absolutePath?.let { alternativeIdePath = it }

	if (!isCI) {
		setPlugins("PsiViewer:193-SNAPSHOT", "java")
		tasks["buildSearchableOptions"]?.enabled = false
	} else setPlugins("java")
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

repositories {
	mavenCentral()
	jcenter()
}

dependencies {
	compile(kotlin("stdlib-jdk8"))
	compile("org.eclipse.mylyn.github", "org.eclipse.egit.github.core", "2.1.5") {
		exclude(module = "gson")
	}
	compile("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.12") {
		exclude(module = "kotlin-stdlib")
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

val generateCode = task<GenMiscTask>("generateCode") {}

val langDir = projectDir.resolve("lang")

fun grammar(name: String): Pair<GenerateParser, GenerateLexer> {
	val lowerCaseName = name.toLowerCase()
	val parserRoot = "org/ice1000/tt/psi/$lowerCaseName"

	val genParser = task<GenerateParser>("gen${name}Parser") {
		generateCode.dependsOn(this)
		group = generateCode.group
		description = "Generate Parser and Psi classes for $name"
		source = "lang/$lowerCaseName.bnf"
		targetRoot = "$buildDir/gen/"
		pathToParser = "$parserRoot${name}Parser.java"
		pathToPsiRoot = parserRoot
		purgeOldFiles = true
	}

	return genParser to task<GenerateLexer>("gen${name}Lexer") {
		generateCode.dependsOn(this)
		group = genParser.group
		description = "Generate Lexer for $name"
		source = "lang/$lowerCaseName.flex"
		targetDir = "build/gen/org/ice1000/tt/psi/$lowerCaseName"
		targetClass = "${name}Lexer"
		purgeOldFiles = true
		dependsOn(genParser)
	}
}

fun utilities(name: String) = task<LangUtilGenTask>("gen${name}Utility") {
	langDataPath = "lang/${name.toLowerCase()}.json"
	generateCode.dependsOn(this)
}

listOf(
	"MiniTT", "ACore", "MLPolyR",
	"RedPrl", "Agda", "CubicalTT",
	"YaccTT", "Voile", "Narc",
	"Mlang", "VitalyR"
).onEach { utilities(it) }.forEach { grammar(it) }
utilities("OwO")

tasks.withType<KotlinCompile> {
	dependsOn(generateCode)
	kotlinOptions {
		jvmTarget = "1.8"
		languageVersion = "1.3"
		apiVersion = "1.3"
		freeCompilerArgs = listOf("-Xjvm-default=enable")
	}
}
