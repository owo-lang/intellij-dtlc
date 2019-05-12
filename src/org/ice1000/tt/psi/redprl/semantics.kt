package org.ice1000.tt.psi.redprl

import icons.SemanticIcons
import javax.swing.Icon

enum class RedPrlSymbolKind(val icon: Icon?) {
	Define(SemanticIcons.PINK_LAMBDA),
	// TODO replace the icon
	Data(SemanticIcons.PINK_LAMBDA),
	Theorem(SemanticIcons.BLUE_T),
	Tactic(SemanticIcons.PURPLE_T),
	Parameter(SemanticIcons.ORANGE_P),
	Unknown(null);
}
