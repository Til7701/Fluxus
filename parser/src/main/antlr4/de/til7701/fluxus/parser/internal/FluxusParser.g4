parser grammar FluxusParser;

options {
    tokenVocab=FluxusLexer;
}

compilationUnit : (importStatement* statement* | typeDefinition) EOF;

statementList : LBRACE statement* RBRACE;

statement
    : LET SymbolIdentifier ASSIGN expression SEMI #variableInitialization
    | expression ASSIGN expression SEMI #assignment
    | expression SEMI #expressionStatement
    | DO? WHEN expression (statement | statementList) #whenStatement
    | RETURN expression? SEMI #returnStatement
    ;

expression
    : SignedIntegerLiteral #signedIntegerLiteralExpression
    | UnsignedIntegerLiteral #unsignedIntegerLiteralExpression
    | FloatLiteral #floatLiteralExpression
    | CharLiteral #charLiteralExpression
    | StringLiteral #stringLiteralExpression
    | BooleanLiteral #booleanLiteralExpression
    | SymbolIdentifier #symbolIdentifierExpression
    | typeIdentifier DOT SymbolIdentifier LPAREN (expression (COMMA expression)*)? RPAREN #staticMethodCall
    | expression DOT SymbolIdentifier LPAREN (expression (COMMA expression)*)? RPAREN #instanceMethodCall
    | typeIdentifier LPAREN (expression (COMMA expression)*)? RPAREN #constructorCall
    | leftUnaryOperator expression #leftUnaryOperationExpression
    | expression binaryOperator expression #binaryOperationExpression
    | LPAREN expression RPAREN #parenExpression
    | LBRACK expression (COMMA expression)* RBRACK #literalArrayCreation
    | LBRACK expression SEMI expression RBRACK #arrayCreation
    | expression LBRACK expression RBRACK #collectionAccess
    | expression AS typeIdentifier #typeCastExpression
    | NEW expression #newExpression
    | expression DOT SymbolIdentifier #instanceFieldAccess
    | typeIdentifier DOT SymbolIdentifier #staticFieldAccess
    ;

leftUnaryOperator
    : SUB
    | BANG
    | HASH
    ;

binaryOperator
    : ADD
    | SUB
    | MUL
    | DIV
    | LT
    | GT
    | LE
    | GE
    | EQUAL
    | NOTEQUAL
    | AND
    | OR
    | IMPL
    | BITAND
    | BITOR
    | CARET
    | SHIFT_LEFT
    | SHIFT_RIGHT
    ;

docComment
    : DOC_COMMENT_LINE+
    ;

typeIdentifier
    : SignedIntegerTypeIdentifier #iTypeIdentifier
    | UnsignedIntegerTypeIdentifier #uTypeIdentifier
    | TypeIdentifier #simpleTypeIdentifier
    | typeIdentifier LBRACK typeIdentifier RBRACK #collectionTypeIdentifier
    ;

importStatement : IMPORT FullyQualifiedTypeIdentifier (AS TypeIdentifier)? SEMI;

typeDefinition
    : classTypeDefinition
    ;

classTypeDefinition
    : importStatement* docComment? CLASS genericTypeList? SEMI fieldDefinition* constructorDefinition* methodDefinition*
    ;

genericTypeList
    : LT typeIdentifier (COMMA typeIdentifier)* GT
    ;

fieldDefinition
    : docComment? fieldModifier* typeIdentifier SymbolIdentifier ASSIGN expression SEMI #initializedFieldDefinition
    ;

fieldModifier
    : STATIC
    ;

constructorDefinition
    : docComment? methodModifier* LPAREN parametherList? RPAREN (statement | statementList)
    ;

methodDefinition
    : docComment? methodModifier* typeIdentifier? SymbolIdentifier LPAREN parametherList? RPAREN (statement | statementList)
    ;

methodModifier
    : STATIC
    ;

parameter
    : typeIdentifier SymbolIdentifier
    ;

parametherList
    : parameter (COMMA parameter)*
    ;
