package org.ice1000.tt.execution

import com.intellij.execution.runners.ExecutionEnvironment

class MlangCommandLineState(
	configuration: MlangRunConfiguration,
	env: ExecutionEnvironment
) : InterpretedCliState<MlangRunConfiguration>(configuration, env) {
	override fun MlangRunConfiguration.pre(params: MutableList<String>) {
		params += listOf("java", "-jar", mlangExecutable)
	}
}
