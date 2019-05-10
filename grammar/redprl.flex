package org.ice1000.tt.psi.redprl;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static org.ice1000.tt.psi.redprl.RedPrlTokenType.*;
import static org.ice1000.tt.psi.redprl.RedPrlTypes.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%{
  public RedPrlLexer() { this((java.io.Reader)null); }

  private int commentStart = 0;
  private int commentDepth = 0;
%}

%public
%class RedPrlLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

%state INSIDE_COMMENT

whitespace = [\ \t\f\r\n]+
upper = [A-Z]
lower = [a-z]
digit = [0-9]
identChr = [a-zA-Z0-9\'/-]

// Regular expressions used:
// s/<INITIAL>\"([a-zA-Z0-9\-]+)\" +=> \(Tokens\.([A-Z_]+)[^;]+;/$1 { return $2; }/rg
// s/<INITIAL>\"([^\"]+)\" +=> \(Tokens\.([A-Z_]+)[^;]+;/"$1" { return $2; }/rg
// JavaScript used:
// .split("\n").sort((a,b)=>b.split(' ')[0].length-a.split(' ')[0].length)
//   .join("\n")

// Slightly modified

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
	[^/\*]+ { }
	\/[^\*]+ { }
	\*[^\/]+ { }
}

"/*" { yybegin(INSIDE_COMMENT)
     ; commentDepth = 1
     ; commentStart = getTokenStart()
     ; }
"//"[^\n]* { return LINE_COMMENT; }
"==>" { return LONG_RIGHT_ARROW; }
"~>" { return SQUIGGLE_RIGHT_ARROW; }
"<~" { return SQUIGGLE_LEFT_ARROW; }
"=>" { return DOUBLE_RIGHT_ARROW; }
":>" { return TRIANGLE_RIGHT; }
"<|" { return LANGLE_PIPE; }
"->" { return RIGHT_ARROW; }
"|>" { return RANGLE_PIPE; }
"||" { return DOUBLE_PIPE; }
"++" { return DOUBLE_PLUS; }
"<-" { return LEFT_ARROW; }
"$" { return DOLLAR_SIGN; }
"`" { return BACK_TICK; }
"&" { return AMPERSAND; }
"{" { return LBRACKET; }
"}" { return RBRACKET; }
"[" { return LSQUARE; }
"@" { return AT_SIGN; }
"]" { return RSQUARE; }
"%" { return PERCENT; }
"(" { return LPAREN; }
")" { return RPAREN; }
"<" { return LANGLE; }
"=" { return EQUALS; }
">" { return RANGLE; }
"_" { return UNDER; }
"^" { return CARET; }
"," { return COMMA; }
":" { return COLON; }
"*" { return TIMES; }
"|" { return PIPE; }
";" { return SEMI; }
"#" { return HASH; }
"!" { return BANG; }
"+" { return PLUS; }
"." { return DOT; }

pushout-rec { return PUSHOUT_REC; }
assumption { return TAC_ASSUMPTION; }
auto-step { return TAC_AUTO_STEP; }
inversion { return TAC_INVERSION; }
coeq-rec { return COEQUALIZER_REC; }
progress { return MTAC_PROGRESS; }
symmetry { return TAC_SYMMETRY; }
discrete { return DISCRETE; }
nat-rec { return NAT_REC; }
negsucc { return NEGSUCC; }
int-rec { return INT_REC; }
pushout { return PUSHOUT; }
without { return WITHOUT; }
extract { return EXTRACT; }
theorem { return THEOREM; }
rewrite { return TAC_REWRITE; }
S1-rec { return S; }
record { return RECORD; }
refine { return REFINE; }
define { return DEFINE; }
tactic { return TACTIC; }
repeat { return MTAC_REPEAT; }
reduce { return TAC_REDUCE; }
unfold { return TAC_UNFOLD; }
tuple { return TUPLE; }
right { return RIGHT; }
cecod { return CECOD; }
cedom { return CEDOM; }
Vproj { return VPROJ; }
claim { return CLAIM; }
exact { return RULE_EXACT; }
match { return MATCH; }
query { return QUERY; }
concl { return CONCL; }
print { return PRINT; }
fcom { return FCOM; }
bool { return BOOL; }
zero { return ZERO; }
succ { return SUCC; }
void { return VOID; }
base { return BASE; }
loop { return LOOP; }
path { return PATH; }
line { return LINE; } 
left { return LEFT; }
glue { return GLUE; }
coeq { return COEQUALIZER; }
self { return SELF; }
ecom { return ECOM; }
hcom { return HCOM; }
then { return THEN; }
else { return ELSE; }
with { return WITH; }
case { return CASE; }
lmax { return LMAX; }
quit { return QUIT; }
data { return DATA; }
fail { return TAC_FAIL; }
auto { return MTAC_AUTO; }
elim { return TAC_ELIM; }
true { return TRUE; }
type { return TYPE; }
nat { return NAT; }
int { return INT; }
pos { return POS; }
lam { return LAMBDA; }
rec { return REC; }
mem { return MEM; }
box { return BOX; }
cap { return CAP; }
Vin { return VIN; }
abs { return ABS; }
com { return COM; }
coe { return COE; }
let { return LET; }
use { return USE; }
dim { return DIM; }
lvl { return LVL; }
knd { return KND; }
exp { return EXP; }
tac { return TAC; }
jdg { return JDG; }
val { return VAL; }
end { return END; }
kan { return KAN; }
pre { return PRE; }
ax { return AX; }
tt { return TT; }
ff { return FF; }
if { return IF; }
S1 { return S; }
ni { return MEM; }
of { return OF; }
by { return BY; }
in { return IN; }
do { return DO; }
fn { return FN; }
id { return TAC_ID; }
at { return AT; }
V { return V; }
U { return UNIVERSE; }

{lower}{identChr} { return VARNAME; }
{upper}{identChr} { return OPNAME; }
\?{identChr} { return HOLENAME; }
\-?{digit}+ { return NUMBERAL; }
[^] { return BAD_CHARACTER; }
