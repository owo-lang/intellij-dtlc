package org.ice1000.tt.project

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.util.SystemInfo
import org.ice1000.tt.executeCommandToFindPath

data class MiniTTSettings(
	var exePath: String = "minittc",
	var version: String = "Unknown"
)

val minittPath by lazy {
	when {
		SystemInfo.isWindows -> findPathWindows() ?: "C:\\Program Files"
		SystemInfo.isMac -> findPathLinux()
		else -> findPathLinux() ?: "/usr/bin/minittc"
	}
}

fun findPathWindows() =
	PathEnvironmentVariableUtil.findInPath("minittc.exe")?.absolutePath
		?: executeCommandToFindPath("where minittc")

fun findPathLinux() =
	PathEnvironmentVariableUtil.findInPath("minittc")?.absolutePath
		?: executeCommandToFindPath("whereis minittc")
