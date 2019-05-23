package org.ice1000.tt.psi.mlpolyr

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import icons.SemanticIcons
import org.ice1000.tt.psi.*
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
	val parent = parent ?: return MLPolyRSymbolKind.Unknown
	return when {
		parent.firstChild?.elementType == MLPolyRTypes.KW_VAL -> MLPolyRSymbolKind.Variable
		parent is MLPolyRRc -> MLPolyRSymbolKind.RcFunction
		parent is MLPolyRFunction ->
			if (this === parent.firstChild) MLPolyRSymbolKind.Function
			else MLPolyRSymbolKind.Parameter
		parent is MLPolyRMr || parent is MLPolyRPat || parent is MLPolyRDtMatch -> MLPolyRSymbolKind.Pattern
		parent is MLPolyRFieldPattern -> MLPolyRSymbolKind.Field
		else -> MLPolyRSymbolKind.Unknown
	}
}

abstract class MLPolyRLabelMixin(node: ASTNode) : GeneralReference(node), MLPolyRLabel {
	override fun handleElementRename(newName: String): PsiElement? =
		replace(MLPolyRTokenType.createLabel(newName, project)
			?: throw IncorrectOperationException("Invalid label: $newName"))

	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingFile as? MLPolyRFileImpl ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		val resolved = file.useConstructors { find { it !== this && it.name == name } }
		file.addConstructor(this)
		return resolved?.let { arrayOf(PsiElementResolveResult(it, true)) } ?: emptyArray()
	}

	override fun getVariants(): Array<LookupElementBuilder> {
		val file = containingFile as? MLPolyRFileImpl ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return file.useConstructors {
			map {
				LookupElementBuilder
					.create(it.text)
					.withIcon(SemanticIcons.BLUE_C)
					.withTypeText("Label")
			}.toTypedArray()
		}
	}
}

abstract class MLPolyRIdentifierMixin(node: ASTNode) : GeneralReference(node), MLPolyRIdentifier {
	override fun handleElementRename(newName: String): PsiElement? =
		replace(MLPolyRTokenType.createIdentifier(newName, project) ?: invalidName(newName))

	override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
		val file = containingFile ?: return emptyArray()
		if (!isValid || project.isDisposed) return emptyArray()
		return ResolveCache.getInstance(project)
			.resolveWithCaching(this, resolver, true, incompleteCode, file)
	}

	override fun getVariants() = resolveWith(PatternCompletionProcessor({
		if ((it as? MLPolyRGeneralPat)?.kind != MLPolyRSymbolKind.Parameter) true
		else PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(it, MLPolyRFunction::class.java)?.exp, this, false)
	}) {
		val parent = it.parent
		val tail = if (parent !is MLPolyRFunction) ""
		else parent.patList.drop(1).joinToString(prefix = " ", separator = " ") { it.bodyText(20) }
		LookupElementBuilder
			.create(it.text)
			.withIcon(it.getIcon(0))
			.withTypeText((it as? MLPolyRGeneralPat)?.kind?.name ?: "??", true)
			.withTailText(tail, true)
	}, this)

	private companion object ResolverHolder {
		val paramFamily = listOf(MLPolyRSymbolKind.Parameter, MLPolyRSymbolKind.Pattern)

		private val resolver = ResolveCache.PolyVariantResolver<MLPolyRIdentifierMixin> { ref, _ ->
			val name = ref.canonicalText
			resolveWith(PatternResolveProcessor(name) {
				if ((it as? MLPolyRGeneralPat)?.kind !in paramFamily) it.text == name
				else it.text == name && PsiTreeUtil.isAncestor(PsiTreeUtil.getParentOfType(it, MLPolyRFunction::class.java)?.exp, ref, false)
			}, ref)
		}
	}
}
