package org.ice1000.tt.psi.redprl

import icons.SemanticIcons
import org.ice1000.tt.psi.elementType
import javax.swing.Icon

enum class RedPrlSymbolKind(val icon: Icon?) {
	Define(SemanticIcons.PINK_LAMBDA),
	Data(SemanticIcons.BLUE_HOLE),
	Value(SemanticIcons.ORANGE_V),
	Theorem(SemanticIcons.BLUE_T),
	Tactic(SemanticIcons.PURPLE_T),
	Parameter(SemanticIcons.ORANGE_P),
	Pattern(SemanticIcons.PURPLE_P),
	Unknown(null);
}

fun RedPrlOpDeclMixin.opSymbolKind() = when (val parent = parent) {
	is RedPrlMlDeclDef -> RedPrlSymbolKind.Define
	is RedPrlMlDeclData -> RedPrlSymbolKind.Data
	is RedPrlMlDeclTactic -> RedPrlSymbolKind.Tactic
	is RedPrlMlDeclTheorem -> RedPrlSymbolKind.Theorem
	is RedPrlDevMatchClauseMixin -> RedPrlSymbolKind.Pattern
	is RedPrlBoundVarOwner -> {
		val aniki = parent.firstChild
		if (aniki?.elementType == RedPrlTypes.LAMBDA
			|| aniki?.nextSibling?.elementType == RedPrlTypes.LAMBDA)
			RedPrlSymbolKind.Parameter
		else RedPrlSymbolKind.Pattern
	}
	is RedPrlMlDeclVal -> RedPrlSymbolKind.Value
	else -> RedPrlSymbolKind.Unknown
}
