parser grammar ConventionalCommitParser;

options { tokenVocab=ConventionalCommitLexer; }

commitMessage
    : message
      EOF
    ;

message
    : header
      (BODY_EMPTY_LINE body)?
      (FOOTER_EMPTY_LINE footers+)?
    ;

header
    : type (LPAREN scope RPAREN)? (breaking)? COLON_SPACE description HEADER_NEWLINE
    ;

type: TYPE;

scope: SCOPE;

breaking: BREAKING;

description: DESCRIPTION;

body
    :  (BODY_EMPTY_LINE | BODY_LINE)+;

footers:
    token FOOTER_SEPARATOR value;

token: FOOTER_TOKEN;

value:
    FOOTER_LINE+;
