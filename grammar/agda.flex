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

WHITE_SPACE=[\ \t\f\r\n]+
IDENTIFIER=[^.;{}()@\ \t\f\r\n][^'.;{}()@\ \t\f\r\n]*
NUMBER=[\u2070-\u20890-9]+
LINE_COMMENT=--[^\n\r]*
STR=\"([^\\\"]|\\t|\\n|\\\"|\\\\)*\"
CHR=\'([^\\\']|\\t|\\n|\\\'|\\\\)+\'
EXP=[eE][+-][0-9]+
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
	[^}{-]+ { }
	[^] { }
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
	[^}{-]+ { }
	[^] { }
}

"{-" { yybegin(INSIDE_COMMENT)
     ; commentDepth = 1
     ; commentStart = getTokenStart()
     ; }
"{!" { yybegin(INSIDE_HOLE)
     ; commentDepth = 1
     ; commentStart = getTokenStart()
     ; }
{LINE_COMMENT} { return LINE_COMMENT; }

∀ { return KEYWORD; }
λ { return KEYWORD; }
no-eta-equality { return KEYWORD; }
eta-equality { return KEYWORD; }
quoteContext { return KEYWORD; }
constructor { return KEYWORD; }
coinductive { return KEYWORD; }
unquoteDecl { return KEYWORD; }
unquoteDef { return KEYWORD; }
postulate { return KEYWORD; }
primitive { return KEYWORD; }
inductive { return KEYWORD; }
quoteGoal { return KEYWORD; }
quoteTerm { return KEYWORD; }
abstract { return KEYWORD; }
instance { return KEYWORD; }
variable { return KEYWORD; }
rewrite { return KEYWORD; }
private { return KEYWORD; }
overlap { return KEYWORD; }
unquote { return KEYWORD; }
pattern { return KEYWORD; }
import { return KEYWORD; }
module { return KEYWORD; }
codata { return KEYWORD; }
record { return KEYWORD; }
infixl { return KEYWORD; }
infixr { return KEYWORD; }
mutual { return KEYWORD; }
forall { return KEYWORD; }
tactic { return KEYWORD; }
syntax { return KEYWORD; }
where { return KEYWORD; }
field { return KEYWORD; }
infix { return KEYWORD; }
macro { return KEYWORD; }
quote { return KEYWORD; }
with { return KEYWORD; }
open { return KEYWORD; }
data { return KEYWORD; }
let { return KEYWORD; }
in { return KEYWORD; }
do { return KEYWORD; }

Set{NUMBER}* { return UNIVERSE; }
Prop{NUMBER}* { return UNIVERSE; }

"..." { return ELLIPSIS; }
".." { return DOT_DOT; }
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
{IDENTIFIER} { return IDENTIFIER; }
// The JFlex compiler says this rule can never be matched!
// [^] { return BAD_CHARACTER; }
