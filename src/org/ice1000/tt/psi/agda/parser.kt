/**
 * **No Agda parser.**
 * This file defines token types needed for Agda lexer.
 */
package org.ice1000.tt.psi.agda

import com.intellij.lexer.FlexAdapter
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.ice1000.tt.AgdaLanguage

class AgdaTokenType(debugName: String) : IElementType(debugName, AgdaLanguage.INSTANCE) {
	companion object Builtin {
		@JvmField val LINE_COMMENT = AgdaTokenType("line comment")
		@JvmField val BLOCK_COMMENT = AgdaTokenType("block comment")
		@JvmField val IDENTIFIER = AgdaTokenType("identifier")
		@JvmField val NUMBER = AgdaTokenType("123")
		@JvmField val FLOAT = AgdaTokenType("3.14")
		@JvmField val HOLE = AgdaTokenType("hole")
		@JvmField val KEYWORD = AgdaTokenType("keyword")
		@JvmField val UNIVERSE = AgdaTokenType("U")
		@JvmField val DOT = AgdaTokenType(".")
		@JvmField val SEMI = AgdaTokenType(";")
		@JvmField val ARROW = AgdaTokenType("->")
		@JvmField val ELLIPSIS = AgdaTokenType("...")
		@JvmField val DOT_DOT = AgdaTokenType("..")
		@JvmField val CLOSE_IDIOM_BRACKET = AgdaTokenType("|)")
		@JvmField val OPEN_IDIOM_BRACKET = AgdaTokenType("(|")
		@JvmField val LAMBDA = AgdaTokenType("\\")
		@JvmField val COLON = AgdaTokenType(":")
		@JvmField val EQUAL = AgdaTokenType("=")
		@JvmField val UNDERSCORE = AgdaTokenType("_")
		@JvmField val QUESTION_MARK = AgdaTokenType("?")
		@JvmField val OPEN_PAREN = AgdaTokenType("(")
		@JvmField val CLOSE_PAREN = AgdaTokenType(")")
		@JvmField val OPEN_BRACE = AgdaTokenType("{")
		@JvmField val CLOSE_BRACE = AgdaTokenType("}")
		@JvmField val BAR = AgdaTokenType("|")
		@JvmField val AS = AgdaTokenType("@")
		@JvmField val CHR_LIT = AgdaTokenType("'")
		@JvmField val STR_LIT = AgdaTokenType("\"")
		@JvmField val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT, HOLE)
		@JvmField val STRINGS = TokenSet.create(CHR_LIT, STR_LIT)
		@JvmField val IDENTIFIERS = TokenSet.create(IDENTIFIER)
	}
}

fun agdaLexer() = FlexAdapter(AgdaLexer())
