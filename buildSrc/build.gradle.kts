import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
group = "org.ice1000.tt.gradle"
version = "114514"
plugins {
	kotlin("jvm") version "1.3.61"
	kotlin("plugin.serialization") version "1.3.61"
}
sourceSets { main { withConvention(KotlinSourceSet::class) { kotlin.srcDirs("src") } } }
repositories { jcenter() }
dependencies {
	compile(kotlin("stdlib-jdk8"))
	compile(kotlin("reflect"))
	compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
}
val compilerArgs = listOf("-Xuse-experimental=kotlin.Experimental")
tasks.withType<KotlinCompile> { kotlinOptions { freeCompilerArgs = compilerArgs } }
