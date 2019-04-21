import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
group = "org.ice1000.tt.gradle"
version = "114514"
plugins { kotlin("jvm") version "1.3.30" }
sourceSets { main { withConvention(KotlinSourceSet::class) { kotlin.srcDirs("src") } } }
repositories { jcenter() }
dependencies { compile(kotlin("stdlib-jdk8")) }
