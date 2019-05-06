package org.ice1000.tt.psi.mlpolyr;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static org.ice1000.tt.psi.mlpolyr.MLPolyRTokenType.*;
import static org.ice1000.tt.psi.mlpolyr.MLPolyRTypes.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

// Reference: https://github.com/owo-lang/MLPolyR/blob/master/mlpolyr.lex

%{
  public MLPolyRLexer() { this((java.io.Reader)null); }

  private int commentStart = 0;
  private int commentDepth = 0;
%}

%public
%class MLPolyRLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

%state INSIDE_COMMENT

WHITE_SPACE=[\ \t\f\r\n]+
ID=[a-zA-Z_'][a-zA-Z_'0-9]*
STR=\"([^\\\"]|\\t|\\n|\\\"|\\\\)*\"
INT=[0-9]+

%%

<INSIDE_COMMENT> {
	"(*" { ++commentDepth; }
	"*)" {
		if (--commentDepth <= 0) {
			yybegin(YYINITIAL);
			zzStartRead = commentStart;
			return COMMENT;
		}
	}
	<<EOF>> {
		yybegin(YYINITIAL);
		zzStartRead = commentStart;
		return COMMENT;
	}
	[^(*]+ { }
	\([^*]+ { }
	\*[^)(*]+ { }
}

"(*" { yybegin(INSIDE_COMMENT)
     ; commentDepth = 1
     ; commentStart = getTokenStart()
     ; }

"..." { return DOTDOTDOT; }
"++" { return PLUSPLUS; }
"{|" { return LCBB; }
"|}" { return RCBB; }
"<=" { return LTEQ; }
"==" { return DEQ; }
"<>" { return NEQ; }
"::" { return DCOLON; }
":=" { return ASSIGN; }
"=>" { return DARROW; }
">=" { return GTEQ; }
\! { return EXCLAM; }
\* { return TIMES; }
\+ { return PLUS; }
\% { return MOD; }
\[ { return LSB; }
\] { return RSB; }
\{ { return LCB; }
\} { return RCB; }
\/ { return DIV; }
\| { return BAR; }
\. { return DOT; }
\( { return LP; }
\) { return RP; }
\= { return EQ; }
\< { return LT; }
- { return MINUS; }
: { return COLON; }
, { return COMMA; }
; { return SEMI; }
_ { return WILD; }
` { return BQ; }
> { return GT; }
rehandling { return KW_REHANDLING; }
handling { return KW_HANDLING; }
default { return KW_DEFAULT; }
nocases { return KW_NOCASES; }
orelse { return KW_ORELSE; }
isnull { return KW_ISNULL; }
false { return KW_FALSE; }
match { return KW_MATCH; }
cases { return KW_CASES; }
where { return KW_WHERE; }
raise { return KW_RAISE; }
then { return KW_THEN; }
else { return KW_ELSE; }
true { return KW_TRUE; }
with { return KW_WITH; }
case { return KW_CASE; }
let { return KW_LET; }
end { return KW_END; }
fun { return KW_FUN; }
and { return KW_AND; }
val { return KW_VAL; }
try { return KW_TRY; }
not { return KW_NOT; }
if { return KW_IF; }
fn { return KW_FN; }
as { return KW_AS; }
of { return KW_OF; }
in { return KW_IN; }
{ID} { return ID; }
{INT} { return INT; }
{STR} { return STR; }
{WHITE_SPACE} { return WHITE_SPACE; }

[^] { return BAD_CHARACTER; }
