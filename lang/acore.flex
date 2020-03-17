package org.ice1000.tt.psi.acore;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static org.ice1000.tt.psi.acore.ACoreTokenType.*;
import static org.ice1000.tt.psi.acore.ACoreTypes.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%{
  public ACoreLexer() { this((java.io.Reader)null); }

  private int commentStart = 0;
  private int commentDepth = 0;
%}

%public
%class ACoreLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

%state INSIDE_COMMENT

WHITE_SPACE=[\ \t\f\r\n]+
IDENTIFIER=[a-zA-Z_][a-zA-Z_0-9]*
COMMENTS = --[^\n\r]*

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
	-+[^}{-]+ { }
}

"{-" { yybegin(INSIDE_COMMENT); commentDepth = 1; commentStart = getTokenStart(); }

= { return EQ; }
: { return COLON; }
, { return COMMA; }
_ { return META_VAR; }
; { return SEMICOLON; }
1 { return ONE_TOKEN; }
0 { return UNIT_TOKEN; }
U { return TYPE_UNIVERSE; }
\* { return MUL; }
\| { return SEP; }
\$ { return DOLLAR; }
\( { return LEFT_PAREN; }
\) { return RIGHT_PAREN; }
\.1 { return DOT_ONE; }
\.2 { return DOT_TWO; }
\.  { return DOT; }
fun { return FUN_TOKEN; }
Sum { return SUM_TOKEN; }
->  { return ARROW; }
\\  { return BACKSLASH; }
Pi  { return PI; }
Sig { return SIGMA; }
letrec { return LETREC_TOKEN; }
Void   { return VOID; }
let    { return LET_TOKEN; }
<YYINITIAL> {COMMENTS} { return LINE_COMMENT; }
{IDENTIFIER}  { return IDENTIFIER; }
{WHITE_SPACE} { return WHITE_SPACE; }

[^] { return BAD_CHARACTER; }
