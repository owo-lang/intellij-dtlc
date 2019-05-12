package org.ice1000.tt.psi.redprl

import com.intellij.util.PlatformIcons
import icons.TTIcons
import javax.swing.Icon

enum class RedPrlSymbolKind(val icon: Icon?) {
	Define(PlatformIcons.FUNCTION_ICON),
	// TODO replace the icon
	Data(PlatformIcons.FUNCTION_ICON),
	Theorem(TTIcons.BLUE_T),
	Tactic(TTIcons.PURPLE_T),
	Parameter(PlatformIcons.PARAMETER_ICON),
	Unknown(null);
}
