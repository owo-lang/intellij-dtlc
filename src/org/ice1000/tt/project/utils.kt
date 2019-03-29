package org.ice1000.tt.project

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.util.SystemInfo
import org.ice1000.tt.executeCommandToFindPath

interface VersionedExecutableSettings {
	var exePath: String
	var version: String
}

data class MiniTTSettings(
	override var exePath: String = "minittc",
	override var version: String = "Unknown"
) : VersionedExecutableSettings

data class AgdaSettings(
	override var exePath: String = "agda",
	override var version: String = "Unknown"
) : VersionedExecutableSettings

fun lazyExePath(exeName: String) = lazy {
	when {
		SystemInfo.isWindows -> findPathWindows(exeName) ?: "C:\\Program Files"
		SystemInfo.isMac -> findPathLinux(exeName)
		else -> findPathLinux(exeName) ?: "/usr/bin/$exeName"
	}
}

val minittPath by lazyExePath("minittc")
val agdaPath by lazyExePath("agda")

fun findPathWindows(exeName: String) =
	PathEnvironmentVariableUtil.findInPath("$exeName.exe")?.absolutePath
		?: executeCommandToFindPath("where $exeName")

fun findPathLinux(exeName: String) =
	PathEnvironmentVariableUtil.findInPath(exeName)?.absolutePath
		?: executeCommandToFindPath("whereis $exeName")
