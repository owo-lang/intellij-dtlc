package org.ice1000.tt.psi.vitalyr;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static org.ice1000.tt.psi.vitalyr.VitalyRTokenType.*;
import static org.ice1000.tt.psi.vitalyr.VitalyRTypes.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%{
  public VitalyRLexer() { this((java.io.Reader)null); }
%}

%public
%class VitalyRLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

WHITE_SPACE=[\ \t\f\r\n]+
IDENTIFIER=[a-zA-Z][a-zA-Z0-9\-'\\/]*
COMMENTS = --[^\n\r]*

%%

= { return EQ; }
\. { return DOT; }
\( { return LPAREN; }
\) { return RPAREN; }
\\ { return BACKSLASH; }

lambda { return KW_LAMBDA; }
{COMMENTS} { return LINE_COMMENT; }
{IDENTIFIER} { return IDENTIFIER; }
{WHITE_SPACE} { return WHITE_SPACE; }

[^] { return BAD_CHARACTER; }
