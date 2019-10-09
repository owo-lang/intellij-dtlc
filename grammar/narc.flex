package org.ice1000.tt.psi.narc;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static org.ice1000.tt.psi.narc.NarcTokenType.*;
import static org.ice1000.tt.psi.narc.NarcTypes.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%{
  public NarcLexer() { this((java.io.Reader)null); }

  private int commentStart = 0;
  private int commentDepth = 0;
%}

%public
%class NarcLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

WHITE_SPACE=[\ \t\f\r\n]+
UNIVERSE = Type
IDENTIFIER=[a-zA-Z][a-zA-Z0-9\-'\\/]*
COMMENTS = \/\/[^\n\r]*

%%

"->" { return ARROW; }
= { return EQ; }
_ { return META; }
; { return SEMI; }
: { return COLON; }
\. { return DOT; }
\( { return LPAREN; }
\) { return RPAREN; }
\{ { return LBRACE; }
\} { return RBRACE; }
\$ { return DOLLAR; }
\|_ { return LINACCESS; }
_\| { return RINACCESS; }

data { return KW_DATA; }
clause { return KW_CLAUSE; }
codata { return KW_CODATA; }
definition { return KW_DEFINITION; }
projection { return KW_PROJECTION; }
constructor { return KW_CONSTRUCTOR; }
{UNIVERSE} { return KW_TYPE; }
{COMMENTS} { return LINE_COMMENT; }
{IDENTIFIER} { return IDENTIFIER; }
{WHITE_SPACE} { return WHITE_SPACE; }

[^] { return BAD_CHARACTER; }
