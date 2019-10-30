package org.ice1000.tt.editing.vitalyr

import org.ice1000.tt.psi.vitalyr.VitalyRAppExpr
import org.ice1000.tt.psi.vitalyr.VitalyRExpr
import org.ice1000.tt.psi.vitalyr.VitalyRLamExpr
import org.ice1000.tt.psi.vitalyr.VitalyRNameUsage

typealias Ctx = MutableMap<String, Term>

enum class ToStrCtx {
	AbsBody,
	AppLhs,
	AppRhs
}

class EvaluationException(message: String) : Exception(message)

fun fromPsi(element: VitalyRExpr): Term = when (element) {
	is VitalyRLamExpr -> Abs(element.nameDecl!!.text, fromPsi(element.expr!!))
	is VitalyRNameUsage -> Var(element.text)
	is VitalyRAppExpr -> element.exprList.asSequence().map(::fromPsi).reduce(::App)
	else -> throw EvaluationException("Bad expression: `${element.text}` of type ${element.javaClass}")
}

sealed class Term {
	@Throws(EvaluationException::class)
	abstract fun bruteEval(ctx: Ctx): Term

	abstract fun <T> fold(init: T, f: (Term, T) -> T): T
	abstract fun toString(builder: StringBuilder, outer: ToStrCtx)
}

data class Var(val name: String) : Term() {
	override fun bruteEval(ctx: Ctx) = ctx[name] ?: this
	override fun <T> fold(init: T, f: (Term, T) -> T) = f(this, init)
	override fun toString(builder: StringBuilder, outer: ToStrCtx) {
		builder.append(name)
	}
}

data class Abs(val name: String, val body: Term) : Term() {
	override fun bruteEval(ctx: Ctx) = Abs(name, body.bruteEval(ctx)).eta()
	override fun <T> fold(init: T, f: (Term, T) -> T) = f(this, f(body, init))
	override fun toString(builder: StringBuilder, outer: ToStrCtx) {
		val paren = outer != ToStrCtx.AbsBody
		if (paren) builder.append('(')
		builder.append('\\').append(name).append(". ")
		body.toString(builder, ToStrCtx.AbsBody)
		if (paren) builder.append(')')
	}

	private fun eta() = if (
		body is App && body.a == Var(name) && body.f.fold(true) { a, b -> b && a == Var(name) }
	) body.f else this
}

data class App(val f: Term, val a: Term) : Term() {
	override fun bruteEval(ctx: Ctx) = when (val f = f.bruteEval(ctx)) {
		is Abs -> f.body.bruteEval(ctx.also { it[f.name] = a })
		else -> App(f, a.bruteEval(ctx))
	}

	override fun <T> fold(init: T, f: (Term, T) -> T) = f(this, f(this.f, f(a, init)))
	override fun toString(builder: StringBuilder, outer: ToStrCtx) {
		val paren = outer == ToStrCtx.AppRhs
		if (paren) builder.append('(')
		f.toString(builder, ToStrCtx.AppLhs)
		builder.append(' ')
		a.toString(builder, ToStrCtx.AppRhs)
		if (paren) builder.append(')')
	}
}

