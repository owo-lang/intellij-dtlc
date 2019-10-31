package org.ice1000.tt.editing.vitalyr

import org.ice1000.tt.psi.vitalyr.VitalyRAppExpr
import org.ice1000.tt.psi.vitalyr.VitalyRExpr
import org.ice1000.tt.psi.vitalyr.VitalyRLamExpr
import org.ice1000.tt.psi.vitalyr.VitalyRNameUsage
import java.util.*

typealias Bind = Pair<String, Term?>
typealias Ctx = LinkedList<Bind>

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
	abstract fun findOccurrence(name: String): Boolean
	abstract fun toString(builder: StringBuilder, outer: ToStrCtx)
	open fun eta() = this
}

data class Var(val name: String) : Term() {
	override fun findOccurrence(name: String) = name == this.name
	override fun toString(builder: StringBuilder, outer: ToStrCtx) {
		builder.append(name)
	}
}

data class Abs(val name: String, val body: Term) : Term() {
	override fun findOccurrence(name: String) =
		name != this.name && body.findOccurrence(name)

	override fun toString(builder: StringBuilder, outer: ToStrCtx) {
		val paren = outer != ToStrCtx.AbsBody
		if (paren) builder.append('(')
		builder.append('\\').append(name).append(". ")
		body.toString(builder, ToStrCtx.AbsBody)
		if (paren) builder.append(')')
	}

	override fun eta() = if (
		body is App && body.a == Var(name) && body.f.findOccurrence(name)
	) body.f.eta() else this
}

data class App(val f: Term, val a: Term) : Term() {
	override fun findOccurrence(name: String) =
		f.findOccurrence(name) && a.findOccurrence(name)

	override fun toString(builder: StringBuilder, outer: ToStrCtx) {
		val paren = outer == ToStrCtx.AppRhs
		if (paren) builder.append('(')
		f.toString(builder, ToStrCtx.AppLhs)
		builder.append(' ')
		a.toString(builder, ToStrCtx.AppRhs)
		if (paren) builder.append(')')
	}
}

