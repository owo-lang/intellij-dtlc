import org.ice1000.tt.gradle.GenMiscTask
import org.ice1000.tt.gradle.LangUtilGenTask
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val isCI = !System.getenv("CI").isNullOrBlank()
val commitHash = kotlin.run {
	val process: Process = Runtime.getRuntime().exec("git rev-parse --short HEAD")
	process.waitFor()
	val output = process.inputStream.use { it.readBytes().let(::String) }
	process.destroy()
	output.trim()
}

val pluginComingVersion = "0.11.0"
val pluginVersion = if (isCI) "$pluginComingVersion-$commitHash" else pluginComingVersion
val packageName = "org.ice1000.tt"

group = packageName
version = pluginVersion

plugins {
	id("com.github.ben-manes.versions") version "0.38.0"
	java
	id("org.jetbrains.intellij") version "1.2.0"
	id("org.jetbrains.grammarkit") version "2021.1.3"
	kotlin("jvm") version "1.5.31"
}

// grammarKit {
// 	grammarKitRelease = "7aecfcd72619e9c241866578e8312f339b4ddbd8"
// }

fun fromToolbox(root: String, ide: String) = file(root)
	.resolve(ide)
	.takeIf { it.exists() }
	?.run { resolve("ch-0").takeIf { it.exists() } ?: resolve("ch-1") }
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
	updateSinceUntilBuild.set(false)
	instrumentCode.set(true)
	// downloadSources.set(true)
	val user = System.getProperty("user.name")
	val os = System.getProperty("os.name")
	when {
		os.startsWith("Windows") -> "C:\\Users\\$user\\AppData\\Local\\JetBrains\\Toolbox\\apps"
		os == "Linux" -> "/home/$user/.local/share/JetBrains/Toolbox/apps"
		else -> null
	}?.let { root ->
		val intellijPath = sequenceOf("IDEA-U", "IDEA-C")
			.mapNotNull { fromToolbox(root, it) }.firstOrNull()
		intellijPath?.absolutePath?.let(localPath::set)
	}

	if (!isCI) {
		plugins.set(listOf("PsiViewer:213-SNAPSHOT", "java"))
		tasks["buildSearchableOptions"]?.enabled = false
	} else plugins.set(listOf("java"))
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<PatchPluginXmlTask> {
	changeNotes.set(file("res/META-INF/change-notes.html").readText())
	pluginDescription.set(file("res/META-INF/description.html").readText())
	version.set(pluginVersion)
	pluginId.set(packageName)
}


sourceSets.main {
	java.srcDirs("src", "$buildDir/gen")
	resources.srcDir("res")
}

sourceSets.test {
	java.srcDir("test")
	resources.srcDir("testData")
}

kotlin {
	sourceSets {
		main {
			kotlin.srcDir("src")
			kotlin.srcDir("$buildDir/gen")
		}
		test {
			kotlin.srcDir("test")
		}
	}
}

repositories {
	mavenCentral()
	maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	implementation("org.eclipse.mylyn.github", "org.eclipse.egit.github.core", "2.1.5") {
		exclude(module = "gson")
	}
	implementation("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.7.2") {
		exclude(module = "kotlin-stdlib")
	}
	testImplementation(kotlin("test-junit"))
	testImplementation("junit", "junit", "4.13.2")
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
	langName = name
	langDataPath = "lang/${name.toLowerCase()}.json"
	generateCode.dependsOn(this)
}

listOf(
	"MiniTT", "ACore", "MLPolyR",
	"RedPrl", "Agda", "CubicalTT",
	"YaccTT", "Voile", "Narc",
	"MiniAgda", "Mlang", "VitalyR"
).onEach { utilities(it) }.forEach { grammar(it) }
utilities("OwO")

tasks.withType<KotlinCompile>().configureEach {
	dependsOn(generateCode)
	kotlinOptions {
		jvmTarget = "11"
		languageVersion = "1.5"
		apiVersion = "1.5"
		freeCompilerArgs = listOf("-Xjvm-default=enable")
	}
}
