package org.ice1000.tt.execution

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionBundle
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.SearchScopeProvider
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware

abstract class InterpretedCliState<T : InterpretedRunConfiguration<out InterpretedCliState<T>>>(
	protected val configuration: T, env: ExecutionEnvironment
) : RunProfileState {
	abstract fun T.pre(params: MutableList<String>)

	override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
		val params = mutableListOf<String>()
		with(configuration) {
			pre(params)
			params += additionalOptions.split(' ', '\t', '\n').filter(String::isNotBlank)
			params += targetFile
		}
		val handler = GeneralCommandLine(params)
			.withCharset(Charsets.UTF_8)
			.withWorkDirectory(configuration.workingDir)
			.let(::OSProcessHandler)
		ProcessTerminatedListener.attach(handler)
		val console = consoleBuilder.console
		console.attachToProcess(handler)
		handler.startNotify()
		return DefaultExecutionResult(console, handler, PauseOutputAction(console, handler))
	}

	private val consoleBuilder = TextConsoleBuilderFactory
		.getInstance()
		.createBuilder(env.project,
			SearchScopeProvider.createSearchScope(env.project, env.runProfile))
}

class PauseOutputAction(private val console: ConsoleView, private val handler: ProcessHandler) :
	ToggleAction(
		ExecutionBundle.message("run.configuration.pause.output.action.name"),
		null, AllIcons.Actions.Pause), DumbAware {
	override fun isSelected(event: AnActionEvent) = console.isOutputPaused
	override fun setSelected(event: AnActionEvent, flag: Boolean) {
		console.isOutputPaused = flag
		ApplicationManager.getApplication().invokeLater { update(event) }
	}

	override fun update(event: AnActionEvent) {
		super.update(event)
		when {
			!handler.isProcessTerminated -> event.presentation.isEnabled = true
			!console.canPause() || !console.hasDeferredOutput() -> event.presentation.isEnabled = false
			else -> {
				event.presentation.isEnabled = true
				console.performWhenNoDeferredOutput { update(event) }
			}
		}
	}
}

class MiniTTCommandLineState(
	configuration: MiniTTRunConfiguration,
	env: ExecutionEnvironment
) : InterpretedCliState<MiniTTRunConfiguration>(configuration, env) {
	override fun MiniTTRunConfiguration.pre(params: MutableList<String>) {
		params += minittExecutable
	}
}

class AgdaCommandLineState(
	configuration: AgdaRunConfiguration,
	env: ExecutionEnvironment
) : InterpretedCliState<AgdaRunConfiguration>(configuration, env) {
	override fun AgdaRunConfiguration.pre(params: MutableList<String>) {
		params += agdaExecutable
	}
}
