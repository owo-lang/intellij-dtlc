import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
group = "org.ice1000.tt.gradle"
version = "114514"
plugins {
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.serialization") version "1.5.31"
}
kotlin { sourceSets { main { kotlin.srcDir("src") } } }
repositories { mavenCentral() }
dependencies {
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("reflect"))
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
}
val compilerArgs = listOf("-Xuse-experimental=kotlin.Experimental")
tasks.withType<KotlinCompile>().configureEach { kotlinOptions { freeCompilerArgs = compilerArgs } }
