parser grammar JavelinParser;

options {
    tokenVocab=JavelinLexer;
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
    | StringLiteral #stringLiteralExpression
    | BooleanLiteral #booleanLiteralExpression
    | SymbolIdentifier #symbolIdentifierExpression
    | typeIdentifier DOT SymbolIdentifier LPAREN (expression (COMMA expression)*)? RPAREN #staticMethodCall
    | expression DOT SymbolIdentifier LPAREN (expression (COMMA expression)*)? RPAREN #instanceMethodCall
    | typeIdentifier LPAREN (expression (COMMA expression)*)? RPAREN #constructorCall
    | leftUnaryOperator expression #leftUnaryOperationExpression
    | expression rightUnaryOperator #rightUnaryOperationExpression
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
    : INC
    | DEC
    | SUB
    | BANG
    | HASH
    ;

rightUnaryOperator
    : INC
    | DEC
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
    ;

typeIdentifier
    : SignedIntegerTypeIdentifier #iTypeIdentifier
    | UnsignedIntegerTypeIdentifier #uTypeIdentifier
    | TypeIdentifier #simpleTypeIdentifier
    | typeIdentifier LBRACK typeIdentifier RBRACK #collectionTypeIdentifier
    | typeIdentifier genericTypeList #genericTypeIdentifier
    ;

importStatement : IMPORT FullyQualifiedTypeIdentifier (AS TypeIdentifier)? SEMI;

typeDefinition
    : classTypeDefinition
    ;

typeModifier
    : PUB
    ;

classTypeDefinition
    : importStatement* typeModifier* CLASS genericTypeList? SEMI fieldDefinition* constructorDefinition* methodDefinition*
    ;

genericTypeList
    : LT typeIdentifier (COMMA typeIdentifier)* GT
    ;

fieldDefinition
    : fieldModifier* typeIdentifier SymbolIdentifier ASSIGN expression SEMI #initializedFieldDefinition
    ;

fieldModifier
    : STATIC
    ;

constructorDefinition
    : methodModifier* LPAREN parametherList? RPAREN (statement | statementList)
    ;

methodDefinition
    : methodModifier* typeIdentifier? SymbolIdentifier LPAREN parametherList? RPAREN (statement | statementList)
    ;

methodModifier
    : STATIC
    | PUB
    ;

parameter
    : typeIdentifier SymbolIdentifier
    ;

parametherList
    : parameter (COMMA parameter)*
    ;
