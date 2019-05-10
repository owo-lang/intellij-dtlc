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
%}

%public
%class RedPrlLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

WHITE_SPACE=[\ \t\f\r\n]+
upper = [A-Z]
lower = [a-z]
digit = [0-9]
identChr = [a-zA-Z0-9\'/-]

%%

{lower}{identChr} { return VARNAME; }
{upper}{identChr} { return OPNAME; }
\?{identChr} { return HOLENAME; }
[^] { return BAD_CHARACTER; }
