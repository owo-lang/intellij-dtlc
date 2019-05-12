package org.ice1000.tt.psi.redprl

import com.intellij.util.PlatformIcons
import icons.SemanticIcons
import javax.swing.Icon

enum class RedPrlSymbolKind(val icon: Icon?) {
	Define(PlatformIcons.FUNCTION_ICON),
	// TODO replace the icon
	Data(PlatformIcons.FUNCTION_ICON),
	Theorem(SemanticIcons.BLUE_T),
	Tactic(SemanticIcons.PURPLE_T),
	Parameter(SemanticIcons.ORANGE_P),
	Unknown(null);
}
