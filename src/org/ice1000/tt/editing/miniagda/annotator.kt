package org.ice1000.tt.editing.miniagda

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import org.ice1000.tt.psi.childrenWithLeaves
import org.ice1000.tt.psi.miniagda.*

object MiniAgdaHighlighter : MiniAgdaGeneratedHighlighter() {
	private val OPERATORS = listOf(
		MiniAgdaTypes.BAR,
		MiniAgdaTypes.COLON,
		MiniAgdaTypes.DOT,
		MiniAgdaTypes.PLUS,
		MiniAgdaTypes.MINUS,
		MiniAgdaTypes.TIMES,
		MiniAgdaTypes.EXPONENT,
		MiniAgdaTypes.ASSIGN,
		MiniAgdaTypes.BACKSLASH,
		MiniAgdaTypes.LT,
		MiniAgdaTypes.LT_EQ,
		MiniAgdaTypes.GT,
		MiniAgdaTypes.INFTY,
		MiniAgdaTypes.SUCC,
		MiniAgdaTypes.TRIANGLE_L,
		MiniAgdaTypes.TRIANGLE_R,
		MiniAgdaTypes.PLUS_PLUS,
		MiniAgdaTypes.ARROW,
		MiniAgdaTypes.AND
	)

	private val KEYWORDS = listOf(
		MiniAgdaTypes.IN,
		MiniAgdaTypes.FUN,
		MiniAgdaTypes.LET,
		MiniAgdaTypes.SET,
		MiniAgdaTypes.MAX,
		MiniAgdaTypes.DATA,
		MiniAgdaTypes.EVAL,
		MiniAgdaTypes.SIZE,
		MiniAgdaTypes.FAIL,
		MiniAgdaTypes.CASE,
		MiniAgdaTypes.SIZED,
		MiniAgdaTypes.COFUN,
		MiniAgdaTypes.CHECK,
		MiniAgdaTypes.COSET,
		MiniAgdaTypes.CODATA,
		MiniAgdaTypes.RECORD,
		MiniAgdaTypes.MUTUAL,
		MiniAgdaTypes.FIELDS,
		MiniAgdaTypes.TRUSTME,
		MiniAgdaTypes.PATTERN,
		MiniAgdaTypes.IMPREDICATIVE
	)

	override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> = when (tokenType) {
		MiniAgdaTypes.IDENTIFIER -> IDENTIFIER_KEY
		MiniAgdaTokenType.LINE_COMMENT -> LINE_COMMENT_KEY
		MiniAgdaTokenType.BLOCK_COMMENT -> BLOCK_COMMENT_KEY
		MiniAgdaTypes.SEMI -> SEMICOLON_KEY
		MiniAgdaTypes.COMMA -> COMMA_KEY
		MiniAgdaTypes.METAVAR -> HOLE_KEY
		MiniAgdaTypes.LPAREN, MiniAgdaTypes.RPAREN -> PAREN_KEY
		MiniAgdaTypes.LBRACE, MiniAgdaTypes.RBRACE -> BRACE_KEY
		MiniAgdaTypes.LBRACK, MiniAgdaTypes.RBRACK -> BRACK_KEY
		in KEYWORDS -> KEYWORD_KEY
		in OPERATORS -> OPERATOR_KEY
		else -> emptyArray()
	}
}

class MiniAgdaAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		when (element) {
			is MiniAgdaDataDecl, is MiniAgdaRecordDecl -> data(element, holder)
			is MiniAgdaFunDecl -> funDecl(element, holder)
		}
	}

	private fun funDecl(element: MiniAgdaFunDecl, holder: AnnotationHolder) {
		element.childrenWithLeaves.firstOrNull { it is MiniAgdaNameDef }?.let {
			holder.createInfoAnnotation(it, null).textAttributes = MiniAgdaGeneratedHighlighter.FUNCTION_NAME
		}
	}

	private fun data(element: PsiElement, holder: AnnotationHolder) {
		element.childrenWithLeaves.firstOrNull { it is MiniAgdaNameDef }?.let {
			holder.createInfoAnnotation(it, null).textAttributes = MiniAgdaGeneratedHighlighter.DATATYPE_NAME
		}
	}
}
