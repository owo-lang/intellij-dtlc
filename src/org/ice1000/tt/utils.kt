package org.ice1000.tt

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

inline fun forceRun(lambda: () -> Unit) = try {
	lambda()
} catch (e: Throwable) {
}

inline fun <T> tryRun(lambda: () -> T): T? = try {
	lambda()
} catch (e: Throwable) {
	null
}

fun validateExe(exePath: String) = try {
	if (exePath.isEmpty()) false
	else Files.isExecutable(Paths.get(exePath))
} catch (e: Exception) {
	false
}

fun versionOf(exePath: String) = executeCommand("$exePath --version")
	.first
	.firstOrNull().orEmpty()

inline fun executeCommandToFindPath(command: String, validate: (String) -> Boolean = ::validateExe) =
	executeCommand(command, null, 500L)
		.first
		.firstOrNull()
		?.split(' ')
		?.firstOrNull(validate)
		?: System.getenv("PATH")
			.split(":")
			.firstOrNull(validate)

fun executeCommand(
	command: String, input: String? = null, timeLimit: Long = 1200L): Pair<List<String>, List<String>> {
	var processRef: Process? = null
	var output: List<String> = emptyList()
	var outputErr: List<String> = emptyList()
	val executor = Executors.newCachedThreadPool()
	val future = executor.submit {
		val process: Process = Runtime.getRuntime().exec(command)
		processRef = process
		process.outputStream.use {
			if (input != null) it.write(input.toByteArray())
			it.flush()
		}
		process.waitFor()
		output = process.inputStream.use(::collectLines)
		outputErr = process.errorStream.use(::collectLines)
		forceRun(process::destroy)
	}
	try {
		future.get(timeLimit, TimeUnit.MILLISECONDS)
	} catch (ignored: Throwable) {
		// timeout? catch it and give up anyway
	} finally {
		processRef?.destroy()
	}
	return output to outputErr
}

private fun collectLines(it: InputStream): List<String> {
	val reader = it.bufferedReader()
	val ret = reader.lines().collect(Collectors.toList())
	forceRun(reader::close)
	return ret
}
