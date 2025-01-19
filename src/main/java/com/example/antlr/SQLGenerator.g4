grammar SQLGenerator;
@header {
    package com.example.antlr;
}

program: tableDef+ EOF;

tableDef: 'table' tableName ':' (columnDef | foreignKeyDef)+;

columnDef: columnName '(' type (',' constraint)* ')';

foreignKeyDef: 'référence:' columnName '->' tableName '.' columnName;

type: 'entier' | 'texte' | 'date';

constraint: 'clé primaire' | 'auto' | 'requis' | 'unique' | 'par défaut:' defaultValue;

defaultValue: 'aujourd\'hui' | 'maintenant' | STRING | INTEGER;

tableName: IDENTIFIER;
columnName: IDENTIFIER;
IDENTIFIER: [a-zA-Z][a-zA-Z0-9]*;
STRING: '"' .*? '"';
INTEGER: [0-9]+;
WHITESPACE: [ \t\r\n]+ -> skip;