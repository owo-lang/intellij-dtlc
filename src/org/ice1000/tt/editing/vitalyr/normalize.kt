package org.ice1000.tt.editing.vitalyr

import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.psi.PsiElement
import com.intellij.util.containers.Stack
import org.ice1000.tt.psi.vitalyr.*

typealias Ctx = Map<String, Term>

sealed class Out {
	data class Abs(val name: String) : Out()
	data class App(val term: Term) : Out()
	data class To(val term: Term) : Out()
}

private var renameCounter = 0

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
				// `wip` is probably canonical now,
				// but the argument might be redex
				stack.push(Out.To(wip))
				wip = top.term
				continue@loop
			}
			is Out.To -> {
				// Maybe it's still canonical, continue wrapping
				wip = App(top.term, wip)
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

fun <T: PsiElement> ensure(element: T?): T = element ?:
	throw EvaluationException("Syntax error in `${element?.text}`")

/// When invoked, the element is guaranteed to have no parsing errors
fun fromPsi(element: VitalyRExpr): Term = when (element) {
	is VitalyRLamExpr -> Abs(ensure(element.nameDecl).text, fromPsi(ensure(element.expr)))
	is VitalyRNameUsage -> Var(element.text)
	is VitalyRParenExpr -> fromPsi(ensure(element.expr))
	is VitalyRAppExpr -> element.exprList.asSequence().map(::fromPsi).reduce(::App)
	else -> throw EvaluationException("Bad expression: `${element.text}` of type ${element.javaClass}")
}

sealed class Term {
	abstract fun findOccurrence(s: String): Boolean
	abstract fun isAbstracting(s: String): Boolean
	abstract fun toString(builder: StringBuilder, outer: ToStrCtx)
	open fun eta() = this
	abstract fun subst(s: String, term: Term): Term
	fun rename(from: String, to: String) = subst(from, Var(to))
	final override fun toString() = buildString { toString(this, ToStrCtx.AppRhs) }
}

data class Var(val name: String) : Term() {
	override fun subst(s: String, term: Term) = if (s == name) term else this
	override fun isAbstracting(s: String) = false
	override fun findOccurrence(s: String) = s == this.name
	override fun toString(builder: StringBuilder, outer: ToStrCtx) {
		builder.append(name)
	}
}

data class Abs(val name: String, val body: Term) : Term() {
	infix fun `$`(term: Term) = body.subst(name, term)
	override fun isAbstracting(s: String) = name == s || body.isAbstracting(s)
	override fun subst(s: String, term: Term) = if (name != s) {
		if (term == Var(name) || term.isAbstracting(name)) {
			val betterName = "${name}${renameCounter++}'"
			Abs(betterName, body.rename(name, betterName).subst(s, term))
		} else Abs(name, body.subst(s, term))
	} else this

	override fun findOccurrence(s: String) = s != name && body.findOccurrence(s)
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
	override fun isAbstracting(s: String) = f.isAbstracting(s) || a.isAbstracting(s)
	override fun findOccurrence(s: String) = f.findOccurrence(s) || a.findOccurrence(s)
	override fun toString(builder: StringBuilder, outer: ToStrCtx) {
		val paren = outer == ToStrCtx.AppRhs
		if (paren) builder.append('(')
		f.toString(builder, ToStrCtx.AppLhs)
		builder.append(' ')
		a.toString(builder, ToStrCtx.AppRhs)
		if (paren) builder.append(')')
	}
}