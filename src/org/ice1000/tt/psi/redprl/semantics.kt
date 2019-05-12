package org.ice1000.tt.psi.redprl

import icons.SemanticIcons
import javax.swing.Icon

enum class RedPrlSymbolKind(val icon: Icon?) {
	Define(SemanticIcons.PINK_LAMBDA),
	Data(SemanticIcons.BLUE_HOLE),
	Value(SemanticIcons.ORANGE_V),
	Theorem(SemanticIcons.BLUE_T),
	Tactic(SemanticIcons.PURPLE_T),
	Parameter(SemanticIcons.ORANGE_P),
	Unknown(null);
}

fun RedPrlOpDeclMixin.opSymbolKind() = when (parent) {
	is RedPrlMlDeclDef -> RedPrlSymbolKind.Define
	is RedPrlMlDeclData -> RedPrlSymbolKind.Data
	is RedPrlMlDeclTactic -> RedPrlSymbolKind.Tactic
	is RedPrlMlDeclTheorem -> RedPrlSymbolKind.Theorem
	is RedPrlMlDeclVal -> RedPrlSymbolKind.Value
	else -> RedPrlSymbolKind.Unknown
}
