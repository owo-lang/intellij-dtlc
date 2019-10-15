package org.ice1000.tt.psi.cubicaltt;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static org.ice1000.tt.psi.cubicaltt.CubicalTTTypes.*;
import static org.ice1000.tt.psi.cubicaltt.CubicalTTTokenType.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%{
  public CubicalTTLexer() { this((java.io.Reader)null); }

  private int commentStart = 0;
  private int commentDepth = 0;
%}

%public
%class CubicalTTLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

%state INSIDE_COMMENT

WHITE_SPACE=[\ \t\f]+
LINE_COMMENT=--[^\n\r]*
EOL=\r\n|\r|\n
IDENTIFIER=[_a-zA-Z][_a-zA-Z'0-9]*

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
	-[^}{-]* { }
}

"{-" { yybegin(INSIDE_COMMENT)
     ; commentDepth = 1
     ; commentStart = getTokenStart()
     ; }
<YYINITIAL> {LINE_COMMENT} { return LINE_COMMENT; }

\/\\ { return AND; }
\\\/ { return OR; }
\.1 { return DOT_ONE; }
\.2 { return DOT_TWO; }
-> { return ARROW; }
\{ { return LAYOUT_START; }
\} { return LAYOUT_END; }
\[ { return LBRACK; }
\\ { return LAMBDA; }
\( { return LPAREN; }
\] { return RBRACK; }
\) { return RPAREN; }
\* { return TIMES; }
\? { return HOLE; }
\| { return BAR; }
\< { return LT; }
\> { return GT; }
; { return LAYOUT_SEP; }
: { return COLON; }
, { return COMMA; }
- { return MINUS; }
0 { return ZERO; }
1 { return ONE; }
@ { return AT; }
= { return EQ; }

transparentAll { return KW_TRANSPARENT_ALL; }
transparent { return KW_TRANSPARENT; }
transport { return KW_TRANSPORT; }
undefined { return KW_UNDEFINED; }
import { return KW_IMPORT; }
module { return KW_MODULE; }
mutual { return KW_MUTUAL; }
opaque { return KW_OPAQUE; }
split@ { return KW_SPLIT_AT; }
unglue { return KW_UNGLUE; }
hComp { return KW_HCOMP; }
hdata { return KW_HDATA; }
PathP { return KW_PATH_P; }
split { return KW_SPLIT; }
where { return KW_WHERE; }
comp { return KW_COMP; }
data { return KW_DATA; }
fill { return KW_FILL; }
glue { return KW_GLUE; }
Glue { return KW_GLUE2; }
with { return KW_WITH; }
idC { return KW_IDC; }
idJ { return KW_IDJ; }
let { return KW_LET; }
Id { return KW_ID; }
in { return KW_IN; }
U { return KW_U; }

{WHITE_SPACE} { return WHITE_SPACE; }
{EOL} { return EOL; }
{IDENTIFIER} { return IDENTIFIER; }
[^] { return BAD_CHARACTER; }
