/* JFlex specification for JavaScript lexical analyzer */
package org.example;

import java.util.HashMap;
import java.util.Map;
import java.awt.Color;

%%

%class JSLexer
%unicode
%line
%column
%type Token

%{
    // Define token types
    public enum TokenType {
        KEYWORD, IDENTIFIER, STRING, NUMBER, COMMENT,
        PUNCTUATION, OPERATOR, WHITESPACE, ERROR
    }

    // Token class to hold token information
    public static class Token {
        private final TokenType type;
        private final String lexeme;
        private final int line;
        private final int column;

        public Token(TokenType type, String lexeme, int line, int column) {
            this.type = type;
            this.lexeme = lexeme;
            this.line = line;
            this.column = column;
        }

        public TokenType getType() {
            return type;
        }

        public String getLexeme() {
            return lexeme;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }

        @Override
        public String toString() {
            return String.format("(%s, '%s', %d:%d)", type, lexeme, line, column);
        }
    }
%}

/* JavaScript keywords */
Keywords = "break" | "case" | "catch" | "class" | "const" | "continue" | "debugger" |
           "default" | "delete" | "do" | "else" | "export" | "extends" | "finally" |
           "for" | "function" | "if" | "import" | "in" | "instanceof" | "new" |
           "return" | "super" | "switch" | "this" | "throw" | "try" | "typeof" |
           "var" | "void" | "while" | "with" | "yield" | "let" | "static" | "async" |
           "await" | "of" | "null" | "true" | "false" | "undefined"

/* JavaScript literals */
Identifier = [a-zA-Z_$][a-zA-Z0-9_$]*
DecInteger = [0-9]+
HexInteger = 0[xX][0-9a-fA-F]+
OctInteger = 0[0-7]+
BinInteger = 0[bB][01]+
Float = ([0-9]+\.[0-9]*|\.[0-9]+)([eE][-+]?[0-9]+)?|[0-9]+[eE][-+]?[0-9]+
Number = {DecInteger} | {HexInteger} | {OctInteger} | {BinInteger} | {Float}

/* Strings */
StringLiteral = (\"([^\\\"\r\n]|\\[^\r\n])*\"|\'([^\\\'\r\n]|\\[^\r\n])*\'|`([^\\`]|\\[^])*`)

/* Comments */
LineComment = "//"[^\r\n]*
BlockComment = "/*" ~"*/"
Comment = {LineComment} | {BlockComment}

/* Operators */
Operator = "+" | "-" | "*" | "/" | "%" | "++" | "--" | "==" | "!=" | "===" | "!==" |
           ">" | "<" | ">=" | "<=" | "&&" | "||" | "!" | "&" | "|" | "^" | "~" |
           "<<" | ">>" | ">>>" | "=" | "+=" | "-=" | "*=" | "/=" | "%=" | "<<=" |
           ">>=" | ">>>=" | "&=" | "|=" | "^=" | "??" | "?." | "??"

/* Punctuation */
Punctuation = "(" | ")" | "{" | "}" | "[" | "]" | ";" | "," | "." | ":" | "?" | "=>" | "..."

/* Whitespace */
WhiteSpace = [ \t\f\r\n]+

%%

/* Token rules */
{Keywords}     { return new Token(TokenType.KEYWORD, yytext(), yyline+1, yycolumn+1); }
{Identifier}   { return new Token(TokenType.IDENTIFIER, yytext(), yyline+1, yycolumn+1); }
{Number}       { return new Token(TokenType.NUMBER, yytext(), yyline+1, yycolumn+1); }
{StringLiteral} { return new Token(TokenType.STRING, yytext(), yyline+1, yycolumn+1); }
{Comment}      { return new Token(TokenType.COMMENT, yytext(), yyline+1, yycolumn+1); }
{Operator}     { return new Token(TokenType.OPERATOR, yytext(), yyline+1, yycolumn+1); }
{Punctuation}  { return new Token(TokenType.PUNCTUATION, yytext(), yyline+1, yycolumn+1); }
{WhiteSpace}   { return new Token(TokenType.WHITESPACE, yytext(), yyline+1, yycolumn+1); }
.              { return new Token(TokenType.ERROR, yytext(), yyline+1, yycolumn+1); }