package org.ice1000.tt.psi.mlpolyr

import com.intellij.util.PlatformIcons
import icons.SemanticIcons
import icons.TTIcons
import org.ice1000.tt.psi.elementType
import javax.swing.Icon

enum class MLPolyRSymbolKind(val icon: Icon?) {
	Function(SemanticIcons.ORANGE_LAMBDA),
	RcFunction(SemanticIcons.PINK_LAMBDA),
	Parameter(SemanticIcons.PURPLE_P),
	Variable(SemanticIcons.ORANGE_V),
	Pattern(SemanticIcons.ORANGE_P),
	Field(SemanticIcons.ORANGE_F),
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
