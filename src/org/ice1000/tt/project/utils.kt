package org.ice1000.tt.project

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.util.SystemInfo
import org.ice1000.tt.executeCommandToFindPath

interface VersionedExecutableSettings {
	var exePath: String
	var version: String
}

fun lazyExePath(exeName: String) = lazy {
	when {
		SystemInfo.isWindows -> findPathWindows(exeName) ?: "C:\\Program Files"
		SystemInfo.isMac -> findPathLinux(exeName)
		else -> findPathLinux(exeName) ?: "/usr/bin/$exeName"
	}
}

fun findPathWindows(exeName: String) =
	PathEnvironmentVariableUtil.findInPath("$exeName.exe")?.absolutePath
		?: executeCommandToFindPath("where $exeName")

fun findPathLinux(exeName: String) =
	PathEnvironmentVariableUtil.findInPath(exeName)?.absolutePath
		?: executeCommandToFindPath("whereis $exeName")
