package org.ice1000.tt.psi.mlpolyr

import com.intellij.util.PlatformIcons
import javax.swing.Icon

enum class SymbolKind(val icon: Icon?) {
	Function(PlatformIcons.FUNCTION_ICON),
	RcFunction(PlatformIcons.FUNCTION_ICON),
	Parameter(PlatformIcons.PARAMETER_ICON),
	Variable(PlatformIcons.VARIABLE_ICON),
	Pattern(PlatformIcons.PROPERTY_ICON),
	Unknown(null);
}
