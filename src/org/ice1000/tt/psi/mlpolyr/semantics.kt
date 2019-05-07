package org.ice1000.tt.psi.mlpolyr

import com.intellij.util.PlatformIcons
import javax.swing.Icon

enum class SymbolKind(val icon: Icon?) {
	Function(PlatformIcons.FUNCTION_ICON),
	RcFunction(PlatformIcons.PROPERTY_ICON),
	Parameter(PlatformIcons.PARAMETER_ICON),
	Variable(PlatformIcons.VARIABLE_ICON),
	Pattern(PlatformIcons.FIELD_ICON),
	Unknown(null);
}
