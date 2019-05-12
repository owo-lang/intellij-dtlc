package org.ice1000.tt.psi.mlpolyr

import com.intellij.util.PlatformIcons
import org.ice1000.tt.psi.elementType
import javax.swing.Icon

enum class MLPolyRSymbolKind(val icon: Icon?) {
	Function(PlatformIcons.FUNCTION_ICON),
	RcFunction(PlatformIcons.FUNCTION_ICON),
	Parameter(PlatformIcons.PARAMETER_ICON),
	Variable(PlatformIcons.VARIABLE_ICON),
	Pattern(PlatformIcons.PROPERTY_ICON),
	Field(PlatformIcons.FIELD_ICON),
	Unknown(null);
}

fun MLPolyRGeneralPat.patSymbolKind(): MLPolyRSymbolKind {
	val parent = parent
	return if (parent != null) when {
		parent.firstChild?.elementType == MLPolyRTypes.KW_VAL -> MLPolyRSymbolKind.Variable
		parent is MLPolyRRc -> MLPolyRSymbolKind.RcFunction
		parent is MLPolyRFunction ->
			if (this === parent.firstChild) MLPolyRSymbolKind.Function
			else MLPolyRSymbolKind.Parameter
		parent is MLPolyRMr || parent is MLPolyRPat || parent is MLPolyRDtMatch -> MLPolyRSymbolKind.Pattern
		parent is MLPolyRFieldPattern -> MLPolyRSymbolKind.Field
		else -> MLPolyRSymbolKind.Unknown
	} else MLPolyRSymbolKind.Unknown
}
