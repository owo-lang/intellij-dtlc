package org.ice1000.tt.psi.voile;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static org.ice1000.tt.psi.voile.VoileTokenType.*;
import static org.ice1000.tt.psi.voile.VoileTypes.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%{
  public VoileLexer() { this((java.io.Reader)null); }

  private int commentStart = 0;
  private int commentDepth = 0;
%}

%public
%class VoileLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

WHITE_SPACE=[\ \t\f\r\n]+
UNIVERSE = Type[0-9]*
IDENTIFIER=[a-zA-Z_\\][a-zA-Z_0-9'\\]*
COMMENTS = \/\/[^\n\r]*
LAMBDA=\\|\u03BB

%%

= { return EQ; }
_ { return META; }
; { return SEMI; }
: { return COLON; }
, { return COMMA; }
\+ { return SUM; }
\. { return DOT; }
\! { return BOT; }
\* { return SIG; }
\( { return LPAREN; }
\) { return RPAREN; }
\$ { return DOLLAR; }
-> { return ARROW; }
\|> { return PIPE; }

let { return KW_LET; }
val { return KW_VAL; }
{LAMBDA} { return LAM; }
{UNIVERSE} { return KW_TYPE; }
{COMMENTS} { return LINE_COMMENT; }
{IDENTIFIER} { return IDENTIFIER; }
@{IDENTIFIER} { return CONS; }
'{IDENTIFIER} { return VARIANT; }
{WHITE_SPACE} { return WHITE_SPACE; }

[^] { return BAD_CHARACTER; }


