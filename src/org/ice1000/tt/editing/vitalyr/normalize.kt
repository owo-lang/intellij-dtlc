package org.ice1000.tt.editing.vitalyr

typealias Ctx = MutableMap<String, Term>

class EvaluationException(message: String) : Exception(message)

sealed class Term {
	@Throws(EvaluationException::class)
	abstract fun bruteEval(ctx: Ctx): Term
}

data class Var(val name: String) : Term() {
	override fun bruteEval(ctx: Ctx) = ctx[name] ?: throw EvaluationException("Unresolved `$name`")
}

data class Abs(val name: String, val body: Term) : Term() {
	override fun bruteEval(ctx: Ctx) = this
}

data class App(val f: Term, val a: Term) : Term() {
	override fun bruteEval(ctx: Ctx) = when (val f = f.bruteEval(ctx)) {
		is Abs -> f.body.bruteEval(ctx.also { it[f.name] = a })
		else -> App(f, a.bruteEval(ctx))
	}
}

