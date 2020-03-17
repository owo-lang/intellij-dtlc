package org.ice1000.tt.psi.agda;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static org.ice1000.tt.psi.agda.AgdaTypes.*;
import static org.ice1000.tt.psi.agda.AgdaTokenType.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%{
  public AgdaLexer() { this((java.io.Reader)null); }

  private int commentStart = 0;
  private int commentDepth = 0;
%}

%public
%class AgdaLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

%state INSIDE_COMMENT
%state INSIDE_HOLE

WHITE_SPACE=[\ \t\f]+
EOL=\r\n|\r|\n
IDENTIFIER=[^.;→{}()@\ \t\f\r\n][^'.;{}()@\ \t\f\r\n]*
NUMBER=[\u2070-\u20890-9]+
LINE_COMMENT=--[^\n\r]*
STR=\"([^\\\"]|\\t|\\n|\\\"|\\\\)*\"
CHR=\'([^\\\']|\\t|\\n|\\\'|\\\\)\'
EXP=[eE][+-]?[0-9]+
FLOAT=-?[0-9]+({EXP}|\.[0-9]({EXP})?)

%%

<INSIDE_COMMENT> {
	"{-" { ++commentDepth; }
	"-}" {
		if (--commentDepth <= 0) {
			yybegin(YYINITIAL);
			zzStartRead = commentStart;
			return BLOCK_COMMENT;
		}
	}
	<<EOF>> {
		yybegin(YYINITIAL);
		zzStartRead = commentStart;
		return BLOCK_COMMENT;
	}
	[^{-]+ { }
	\{[^-]+ { }
	-[^}{-]+ { }
}

<INSIDE_HOLE> {
	"{!" { ++commentDepth; }
	"!}" {
		if (--commentDepth <= 0) {
			yybegin(YYINITIAL);
			zzStartRead = commentStart;
			return HOLE;
		}
	}
	<<EOF>> {
		yybegin(YYINITIAL);
		zzStartRead = commentStart;
		return HOLE;
	}
	[^{!]+ { }
	\{[^!]+ { }
	\![^}{!]* { }
}

\{-#([^#]|#[^-])*#-\} { return PRAGMA; }
"{-" { yybegin(INSIDE_COMMENT)
     ; commentDepth = 1
     ; commentStart = getTokenStart()
     ; }
"{!" { yybegin(INSIDE_HOLE)
     ; commentDepth = 1
     ; commentStart = getTokenStart()
     ; }
<YYINITIAL> {LINE_COMMENT} { return LINE_COMMENT; }

∀ { return KW_FORALL; }
λ { return LAMBDA; }
no-eta-equality { return KW_NO_ETA_EQUALITY; }
eta-equality { return KW_ETA_EQUALITY; }
quoteContext { return KW_QUOTE_CONTEXT; }
constructor { return KW_CONSTRUCTOR; }
coinductive { return KW_COINDUCTIVE; }
unquoteDecl { return KW_UNQUOTE_DECL; }
unquoteDef { return KW_UNQUOTE_DEF; }
postulate { return KW_POSTULATE; }
primitive { return KW_PRIMITIVE; }
inductive { return KW_INDUCTIVE; }
quoteGoal { return KW_QUOTE_GOAL; }
quoteTerm { return KW_QUOTE_TERM; }
variable { return KW_VARIABLE; }
abstract { return KW_ABSTRACT; }
instance { return KW_INSTANCE; }
rewrite { return KW_REWRITE; }
private { return KW_PRIVATE; }
overlap { return KW_OVERLAP; }
unquote { return KW_UNQUOTE; }
pattern { return KW_PATTERN; }
import { return KW_IMPORT; }
module { return KW_MODULE; }
codata { return KW_CODATA; }
record { return KW_RECORD; }
infixl { return KW_INFIXL; }
infixr { return KW_INFIXR; }
mutual { return KW_MUTUAL; }
forall { return KW_FORALL; }
tactic { return KW_TACTIC; }
syntax { return KW_SYNTAX; }
where { return KW_WHERE; }
field { return KW_FIELD; }
infix { return KW_INFIX; }
macro { return KW_MACRO; }
quote { return KW_QUOTE; }
with { return KW_WITH; }
open { return KW_OPEN; }
data { return KW_DATA; }
let { return KW_LET; }
in { return KW_IN; }
do { return KW_DO; }
hiding { return KW_HIDING; }
renaming { return KW_RENAMING; }
using { return KW_USING; }

Set{NUMBER}* { return UNIVERSE; }
Prop{NUMBER}* { return UNIVERSE; }

"..." { return ELLIPSIS; }
//".." { return DOT_DOT; }
"|)" { return CLOSE_IDIOM_BRACKET; }
"⦈" { return CLOSE_IDIOM_BRACKET; }
"(|" { return OPEN_IDIOM_BRACKET; }
"⦇" { return OPEN_IDIOM_BRACKET; }
"\\" { return LAMBDA; }
"->" { return ARROW; }
"." { return DOT; }
";" { return SEMI; }
":" { return COLON; }
"=" { return EQUAL; }
"_" { return UNDERSCORE; }
"?" { return QUESTION_MARK; }
"(" { return OPEN_PAREN; }
")" { return CLOSE_PAREN; }
"{" { return OPEN_BRACE; }
"}" { return CLOSE_BRACE; }
"|" { return BAR; }
"@" { return AS; }
"→" { return ARROW; }

{CHR} { return CHR_LIT; }
{STR} { return STR_LIT; }
{FLOAT} { return FLOAT; }
{NUMBER} { return NUMBER; }
{WHITE_SPACE} { return WHITE_SPACE; }
{EOL} { return EOL; }
{IDENTIFIER} { return IDENTIFIER; }
// The JFlex compiler says this rule can never be matched!
// [^] { return BAD_CHARACTER; }
