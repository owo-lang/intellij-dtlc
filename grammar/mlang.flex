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
%}

%public
%class MlangLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

WHITE_SPACE=[\ \t\f\r\n\|]+
IDENTIFIER=[a-zA-Z][a-zA-Z_0-9]*

%%

= { return EQ; }
# { return HASH; }
: { return COLON; }
, { return COMMA; }
\{ { return LBRACE; }
\( { return LPAREN; }
\} { return RBRACE; }
\) { return RPAREN; }
_ { return META_VAR; }
define { return KW_DEFINE; }
__debug { return KW_DEBUG; }
declare { return KW_DECLARE; }
parameters { return KW_PARAMETERS; }
inductively { return KW_INDUCTIVELY; }
with_constructor { return KW_WITH_CONSTRUCTOR; }
{IDENTIFIER} { return IDENTIFIER; }

{WHITE_SPACE}   { return WHITE_SPACE; }

[^] { return BAD_CHARACTER; }
