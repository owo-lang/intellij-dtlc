package org.ice1000.tt.editing.vitalyr

import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.util.containers.Stack
import org.ice1000.tt.psi.vitalyr.*

typealias Ctx = Map<String, Term>

sealed class Out {
	data class Abs(val name: String) : Out()
	data class App(val term: Term) : Out()
}

fun normalize(term: Term, scope: Ctx): Term {
	// The term we're working on
	var wip = term
	// The execution stack
	val stack = Stack<Out>()
	loop@ while (true) {
		// First, head into the innermost term
		doing@ while (true) {
			ProgressIndicatorProvider.checkCanceled()
			when (wip) {
				is Abs -> {
					// Obtaining the information that we're working inside of an abstraction
					stack.push(Out.Abs(wip.name))
					// Start working on the inner term
					wip = wip.body
				}
				is App -> {
					// Remember to work on the argument later on
					stack.push(Out.App(wip.a))
					// Start working on the term being applied
					wip = wip.f
				}
				is Var -> {
					val name = wip.name
					// We're reaching an abstraction variable, therefore already normal
					if (Out.Abs(name) in stack) break@doing
					// Look for global declaration of this name
					wip = scope[name] ?: throw EvaluationException("Unresolved `$name`")
				}
			}
		}
		// Now `wip` is the innermost canonical term

		// Second, try wrapping it with `Abs`, until we reach an `App`
		doing@ while (stack.isNotEmpty()) when (val top = stack.pop()) {
			is Out.Abs -> wip = Abs(top.name, wip).eta()
			// Do the application
			is Out.App -> if (wip is Abs) {
				// By doing this, we have a potential redex
				wip = wip `$` top.term
				// Go to next loop and normalize again
				continue@loop
			} else {
				// Maybe it's still canonical, continue wrapping
				wip = App(wip, if (top.term is Var && top.term.name in scope)
					scope.getValue(top.term.name)
				else top.term)
				continue@doing
			}
		}
		if (stack.isEmpty()) break@loop
	}
	return wip
}

// Below: not recommended for reading

enum class ToStrCtx {
	AbsBody,
	AppLhs,
	AppRhs
}

class EvaluationException(message: String) : Exception(message)

/// When invoked, the element is guaranteed to have no parsing errors
fun fromPsi(element: VitalyRExpr): Term = when (element) {
	is VitalyRLamExpr -> Abs(element.nameDecl!!.text, fromPsi(element.expr!!))
	is VitalyRNameUsage -> Var(element.text)
	is VitalyRParenExpr -> fromPsi(element.expr!!)
	is VitalyRAppExpr -> element.exprList.asSequence().map(::fromPsi).reduce(::App)
	else -> throw EvaluationException("Bad expression: `${element.text}` of type ${element.javaClass}")
}

sealed class Term {
	abstract fun findOccurrence(name: String): Boolean
	abstract fun toString(builder: StringBuilder, outer: ToStrCtx)
	open fun eta() = this
	abstract fun subst(s: String, term: Term): Term
	fun rename(from: String, to: String) = subst(from, Var(to))
}

data class Var(val name: String) : Term() {
	override fun subst(s: String, term: Term) = if (s == name) term else this
	override fun findOccurrence(name: String) = name == this.name
	override fun toString(builder: StringBuilder, outer: ToStrCtx) {
		builder.append(name)
	}
}

data class Abs(val name: String, val body: Term) : Term() {
	infix fun `$`(term: Term) = body.subst(name, term)
	override fun subst(s: String, term: Term) = if (name != s) {
		if (term == Var(name)) {
			val betterName = "${name}'"
			Abs(betterName, body.rename(name, betterName).subst(s, term))
		} else Abs(name, body.subst(s, term))
	} else this

	override fun findOccurrence(name: String) = name != this.name && body.findOccurrence(name)
	override fun toString(builder: StringBuilder, outer: ToStrCtx) {
		val paren = outer != ToStrCtx.AbsBody
		if (paren) builder.append('(')
		builder.append('\\').append(name).append(". ")
		body.toString(builder, ToStrCtx.AbsBody)
		if (paren) builder.append(')')
	}

	override fun eta() = if (
		body is App && body.a == Var(name) && !body.f.findOccurrence(name)
	) body.f else this
}

data class App(val f: Term, val a: Term) : Term() {
	override fun subst(s: String, term: Term) = App(f.subst(s, term), a.subst(s, term))
	override fun findOccurrence(name: String) = f.findOccurrence(name) || a.findOccurrence(name)
	override fun toString(builder: StringBuilder, outer: ToStrCtx) {
		val paren = outer == ToStrCtx.AppRhs
		if (paren) builder.append('(')
		f.toString(builder, ToStrCtx.AppLhs)
		builder.append(' ')
		a.toString(builder, ToStrCtx.AppRhs)
		if (paren) builder.append(')')
	}
}