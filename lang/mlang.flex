package org.ice1000.tt.psi.mlang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static org.ice1000.tt.psi.mlang.MlangTokenType.*;
import static org.ice1000.tt.psi.mlang.MlangTypes.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%{
  public MlangLexer() { this((java.io.Reader)null); }

  private int commentStart = 0;
  private int commentDepth = 0;
%}

%public
%class MlangLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

%state INSIDE_COMMENT

WHITE_SPACE=[\ \t\f\r\n│]+
IDENTIFIER=[a-zA-Z_][a-zA-Z_0-9]*

%%

<INSIDE_COMMENT> {
	"/*" { ++commentDepth; }
	"*/" { if (--commentDepth <= 0) {
	         yybegin(YYINITIAL);
	         zzStartRead = commentStart;
	         return BLOCK_COMMENT;
	       } }
	<<EOF>> {
		yybegin(YYINITIAL);
		zzStartRead = commentStart;
		return BLOCK_COMMENT;
	}
	[^/*]+ { }
	\/[^*]+ { }
	\*[^/*]* { }
}

"/*" { yybegin(INSIDE_COMMENT)
     ; commentDepth = 1
     ; commentStart = getTokenStart()
     ; }
"//"[^\n]* { return LINE_COMMENT; }
= { return EQ; }
@ { return AT; }
∨ { return OR; }
∧ { return AND; }
┬ { return TOP; }
# { return HASH; }
\~ { return NEG; }
\. { return DOT; }
\| { return BAR; }
⊏ { return MATRIX_SING; }
┌ { return MATRIX_START; }
├ { return MATRIX_CONT; }
└ { return MATRIX_END; }
: { return COLON; }
, { return COMMA; }
\^ { return LIFT; }
→ { return ARROW; }
⇒ { return DARROW; }
[01] { return DIM; }
─ { return IGNORED; }
\{ { return LBRACE; }
\( { return LPAREN; }
\} { return RBRACE; }
\) { return RPAREN; }
\[ { return LBRACK; }
\] { return RBRACK; }
_ { return META_VAR; }
≡ { return TRIPLE_EQ; }
as { return KW_AS; }
run { return KW_RUN; }
sum { return KW_SUM; }
case { return KW_CASE; }
glue_type { return KW_GLUE_TYPE; }
glue { return KW_GLUE; }
type { return KW_TYPE; }
make { return KW_MAKE; }
fill { return KW_FILL; }
comp { return KW_COMP; }
I { return KW_INTERVAL; }
hcomp { return KW_HCOMP; }
hfill { return KW_HFILL; }
field { return KW_FIELD; }
define { return KW_DEFINE; }
record { return KW_RECORD; }
transp { return KW_TRANSP; }
unglue { return KW_UNGLUE; }
__debug { return KW_DEBUG; }
"???" { return KW_UNDEFINED; }
declare { return KW_DECLARE; }
parameters { return KW_PARAMETERS; }
inductively { return KW_INDUCTIVELY; }
with_constructor { return KW_WITH_CONSTRUCTOR; }
{IDENTIFIER} { return IDENTIFIER; }

{WHITE_SPACE} { return WHITE_SPACE; }

[^] { return BAD_CHARACTER; }
