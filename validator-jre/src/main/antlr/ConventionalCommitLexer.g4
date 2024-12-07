lexer grammar ConventionalCommitLexer;

fragment ALPHANUMERIC_CHAR: [_\p{L}\p{Nd}] [\p{M}]*;
fragment IDENTIFIER: (ALPHANUMERIC_CHAR+ (ALPHANUMERIC_CHAR | '-' | '_')* ALPHANUMERIC_CHAR+) | ALPHANUMERIC_CHAR+;
fragment FOOTER_TOKEN_FRAGMENT: 'BREAKING CHANGE' | IDENTIFIER;
fragment FOOTER_SEPARATOR_FRAGMENT: ': ' | ' #';

TYPE       : IDENTIFIER;
LPAREN     : '(' -> pushMode(SCOPE_MODE);
COLON_SPACE      : ':' ' ' ->  pushMode(DESCRIPTION_MODE);
HEADER_NEWLINE    : '\n' {_input.mark();} -> pushMode(BODY_MODE);
BREAKING : '!' ;

mode SCOPE_MODE;
RPAREN     : ')' ->  popMode;
SCOPE      : IDENTIFIER;

mode DESCRIPTION_MODE;
DESCRIPTION : ~[\n]+ -> popMode;

mode BODY_MODE;
BODY_EMPTY_LINE: '\n';
BODY_FOOTER_START_LINE: '\n' FOOTER_TOKEN_FRAGMENT FOOTER_SEPARATOR_FRAGMENT ~[\n]* '\n' { _input.seek(_input.index() - getText().length());} -> skip, popMode, pushMode(FOOTER_TOKEN_MODE);
BODY_LINE : ~[\n]* '\n' ;

mode FOOTER_TOKEN_MODE;
FOOTER_EMPTY_LINE: '\n';
FOOTER_TOKEN: FOOTER_TOKEN_FRAGMENT;
FOOTER_SEPARATOR: FOOTER_SEPARATOR_FRAGMENT -> popMode, pushMode(FOOTER_VALUE_MODE);

mode FOOTER_VALUE_MODE;
NEXT_FOOTER_START_LINE: FOOTER_TOKEN_FRAGMENT FOOTER_SEPARATOR_FRAGMENT ~[\n]* '\n' { _input.seek(_input.index() - getText().length());} -> skip, popMode, pushMode(FOOTER_TOKEN_MODE);
FOOTER_LINE : ~[\n]* '\n';
