package org.ice1000.tt.editing.vitalyr

import org.ice1000.tt.psi.vitalyr.VitalyRAppExpr
import org.ice1000.tt.psi.vitalyr.VitalyRExpr
import org.ice1000.tt.psi.vitalyr.VitalyRLamExpr
import org.ice1000.tt.psi.vitalyr.VitalyRNameUsage
import java.lang.StringBuilder

typealias Ctx = MutableMap<String, Term>

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

	abstract fun toString(builder: StringBuilder, outer: Term?)
}

data class Var(val name: String) : Term() {
	override fun bruteEval(ctx: Ctx) = ctx[name] ?: this
	override fun toString(builder: StringBuilder, outer: Term?) {
		builder.append(name)
	}
}

data class Abs(val name: String, val body: Term) : Term() {
	override fun bruteEval(ctx: Ctx) = Abs(name, body.bruteEval(ctx))
	override fun toString(builder: StringBuilder, outer: Term?) {
		val paren = outer is App
		if (paren) builder.append('(')
		builder.append('\\').append(name).append(". ")
		body.toString(builder, this)
		if (paren) builder.append(')')
	}
}

data class App(val f: Term, val a: Term) : Term() {
	override fun bruteEval(ctx: Ctx) = when (val f = f.bruteEval(ctx)) {
		is Abs -> f.body.bruteEval(ctx.also { it[f.name] = a })
		else -> App(f, a.bruteEval(ctx))
	}

	override fun toString(builder: StringBuilder, outer: Term?) {
		val paren = outer is App
		if (paren) builder.append('(')
		f.toString(builder, this)
		builder.append(' ')
		a.toString(builder, this)
		if (paren) builder.append(')')
	}
}

