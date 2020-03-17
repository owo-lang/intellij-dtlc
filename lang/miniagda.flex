package org.ice1000.tt.psi.miniagda;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static org.ice1000.tt.psi.miniagda.MiniAgdaTypes.*;
import static org.ice1000.tt.psi.miniagda.MiniAgdaTokenType.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%{
  public MiniAgdaLexer() { this((java.io.Reader)null); }

  private int commentStart = 0;
  private int commentDepth = 0;
%}

%public
%class MiniAgdaLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

%state INSIDE_COMMENT

WHITE_SPACE=[\ \r\n\t\f]+
LINE_COMMENT=--[^\n\r]*
IDENTIFIER=[a-zA-Z][_a-zA-Z'0-9]*

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

"{-" { yybegin(INSIDE_COMMENT)
     ; commentDepth = 1
     ; commentStart = getTokenStart()
     ; }
<YYINITIAL> {LINE_COMMENT} { return LINE_COMMENT; }

"<|" { return TRIANGLE_L; }
"|>" { return TRIANGLE_R; }
\+\+ { return PLUS_PLUS; }
\# { return INFTY; }
\$ { return SUCC; }
"->" { return ARROW; }
"<=" { return LT_EQ; }
\{ { return LBRACE; }
\} { return RBRACE; }
\[ { return LBRACK; }
\] { return RBRACK; }
\( { return LPAREN; }
\) { return RPAREN; }
\| { return BAR; }
\; { return SEMI; }
\: { return COLON; }
\, { return COMMA; }
\. { return DOT; }
\+ { return PLUS; }
\- { return MINUS; }
//\/ { return DIVIDE; }
\* { return TIMES; }
\^ { return EXPONENT; }
\& { return AND; }
=  { return ASSIGN; }
\\ { return BACKSLASH; }
\_ { return METAVAR; }
\< { return LT; }
\> { return GT; }
in  { return IN ; }
fun { return FUN; }
//def { return DEF; }
let { return LET; }
Set { return SET; }
max { return MAX; }
data { return DATA; }
eval { return EVAL; }
//Type { return TYPE; }
Size { return SIZE; }
fail { return FAIL; }
case { return CASE; }
sized { return SIZED; }
cofun { return COFUN; }
check { return CHECK; }
CoSet { return COSET; }
codata { return CODATA; }
record { return RECORD; }
mutual { return MUTUAL; }
fields { return FIELDS; }
trustme { return TRUSTME; }
pattern { return PATTERN; }
impredicative { return IMPREDICATIVE; }

{WHITE_SPACE} { return WHITE_SPACE; }
[0-9]+ { return NUMBER; }
// There's qualified name parsing in MiniAgda but I doubt it's
// really used :(
{IDENTIFIER} { return IDENTIFIER; }
[^] { return BAD_CHARACTER; }
