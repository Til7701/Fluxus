package de.til7701.fluxus.parser.internal;

import de.til7701.fluxus.ast.*;
import de.til7701.fluxus.parser.ParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@NullMarked
@RequiredArgsConstructor
public final class Walker extends FluxusParserBaseVisitor<Node> {

    private final String sourceName;

    private Span createSpan(ParserRuleContext ctx) {
        return new Span(
                sourceName,
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length()
        );
    }

    private Span createSpan(ParserRuleContext ctx1, ParserRuleContext ctx2) {
        return new Span(
                sourceName,
                ctx1.getStart().getLine(),
                ctx1.getStart().getCharPositionInLine(),
                ctx2.getStop().getLine(),
                ctx2.getStop().getCharPositionInLine() + ctx2.getStop().getText().length()
        );
    }

    private Span createSpan(Token token) {
        return new Span(
                sourceName,
                token.getLine(),
                token.getCharPositionInLine(),
                token.getLine(),
                token.getCharPositionInLine() + token.getText().length()
        );
    }

    private DocComment readDocComment(FluxusParser.DocCommentContext ctx) {
        StringBuilder comment = new StringBuilder();
        for (TerminalNode line : ctx.DOC_COMMENT_LINE()) {
            comment.append(line.getText().substring(3));
        }
        return new DocComment(createSpan(ctx), comment.toString().strip());
    }

    @Override
    public Node visitCompilationUnit(FluxusParser.CompilationUnitContext ctx) {
        if (ctx.typeDefinition() != null) {
            return visit(ctx.typeDefinition());
        } else {
            List<Statement> statements = Stream.concat(
                            ctx.importStatement().stream()
                                    .map(importContext -> (Statement.Import) visit(importContext)),
                            ctx.statement().stream()
                                    .map(statementContext -> (Statement) visit(statementContext))
                    )
                    .toList();

            return new Script(
                    createSpan(ctx),
                    statements
            );
        }
    }

    @Override
    public Node visitStatementList(FluxusParser.StatementListContext ctx) {
        return new Statement.StatementList(
                createSpan(ctx),
                ctx.statement().stream()
                        .map(tree -> (Statement) visit(tree))
                        .toList()
        );
    }

    @Override
    public Node visitVariableInitialization(FluxusParser.VariableInitializationContext ctx) {
        return new Statement.VariableInitialization(
                createSpan(ctx),
                ctx.SymbolIdentifier().getText(),
                (Expression) visit(ctx.expression())
        );
    }

    @Override
    public Node visitAssignment(FluxusParser.AssignmentContext ctx) {
        return new Statement.Assignment(
                createSpan(ctx),
                (Expression) visit(ctx.expression(0)),
                (Expression) visit(ctx.expression(1))
        );
    }

    @Override
    public Node visitExpressionStatement(FluxusParser.ExpressionStatementContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Node visitWhenStatement(FluxusParser.WhenStatementContext ctx) {
        Statement body;
        if (ctx.statement() != null)
            body = (Statement) visit(ctx.statement());
        else
            body = (Statement) visit(ctx.statementList());
        return new Statement.When(
                createSpan(ctx),
                ctx.DO() != null,
                (Expression) visit(ctx.expression()),
                body
        );
    }

    @Override
    public Node visitReturnStatement(FluxusParser.ReturnStatementContext ctx) {
        return new Statement.Return(
                createSpan(ctx),
                (Expression) visit(ctx.expression())
        );
    }

    @Override
    public Node visitUnsignedIntegerLiteralExpression(FluxusParser.UnsignedIntegerLiteralExpressionContext ctx) {
        String literal = ctx.UnsignedIntegerLiteral().getText();
        String[] split = literal.split("U", 2);
        long value = switch (split[0]) {
            case "MAX" -> 0xffffffffffffffffL;
            default -> {
                String number = split[0];
                int radix = 10;
                if (number.startsWith("0b")) {
                    number = number.substring(2);
                    radix = 2;
                } else if (number.startsWith("0x")) {
                    number = number.substring(2);
                    radix = 16;
                }
                yield Long.parseUnsignedLong(number, radix);
            }
        };
        return new Expression.UnsignedIntegerLiteral(
                createSpan(ctx),
                value,
                Long.parseLong(split[1])
        );
    }

    @Override
    public Node visitSignedIntegerLiteralExpression(FluxusParser.SignedIntegerLiteralExpressionContext ctx) {
        String literal = ctx.SignedIntegerLiteral().getText();
        String[] split = literal.split("I", 2);
        long value = switch (split[0]) {
            case "MAX" -> Long.MAX_VALUE;
            default -> Long.parseLong(split[0].toLowerCase(Locale.ROOT));
        };
        return new Expression.SignedIntegerLiteral(
                createSpan(ctx),
                value,
                Long.parseLong(split[1])
        );
    }

    @Override
    public Node visitFloatLiteralExpression(FluxusParser.FloatLiteralExpressionContext ctx) {
        String literal = ctx.FloatLiteral().getText();
        String[] split = literal.split("F", 2);
        double value = switch (split[0]) {
            case "MAX" -> Double.MAX_VALUE;
            default -> Double.parseDouble(split[0].toLowerCase(Locale.ROOT));
        };
        return new Expression.FloatLiteral(
                createSpan(ctx),
                value,
                Long.parseLong(split[1])
        );
    }

    @Override
    public Node visitCharLiteralExpression(FluxusParser.CharLiteralExpressionContext ctx) {
        String text = ctx.CharLiteral().getText();
        return new Expression.CharLiteral(
                createSpan(ctx),
                text.substring(1, text.length() - 1)
        );
    }

    @Override
    public Node visitStringLiteralExpression(FluxusParser.StringLiteralExpressionContext ctx) {
        String text = ctx.StringLiteral().getText();
        return new Expression.StringLiteral(
                createSpan(ctx),
                text.substring(1, text.length() - 1)
        );
    }

    @Override
    public Node visitBooleanLiteralExpression(FluxusParser.BooleanLiteralExpressionContext ctx) {
        Span span = createSpan(ctx);
        boolean value = switch (ctx.BooleanLiteral().getText()) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new ParserException(span, "Invalid boolean literal: " + ctx.BooleanLiteral().getText());
        };
        return new Expression.BooleanLiteral(
                span,
                value
        );
    }

    @Override
    public Node visitSymbolIdentifierExpression(FluxusParser.SymbolIdentifierExpressionContext ctx) {
        return new Expression.Symbol(
                createSpan(ctx),
                ctx.SymbolIdentifier().getText()
        );
    }

    @Override
    public Node visitStaticMethodCall(FluxusParser.StaticMethodCallContext ctx) {
        List<Expression> args = ctx.expression().stream()
                .map(c -> (Expression) visit(c))
                .toList();
        Type type = (Type) visit(ctx.typeIdentifier());
        return new Expression.StaticMethodCall(
                createSpan(ctx),
                type,
                ctx.SymbolIdentifier().getText(),
                args
        );
    }

    @Override
    public Node visitInstanceMethodCall(FluxusParser.InstanceMethodCallContext ctx) {
        List<Expression> args = ctx.expression().subList(1, ctx.expression().size()).stream()
                .map(c -> (Expression) visit(c))
                .toList();
        return new Expression.InstanceMethodCall(
                createSpan(ctx),
                (Expression) visit(ctx.expression(0)),
                ctx.SymbolIdentifier().getText(),
                args
        );
    }

    @Override
    public Node visitConstructorCall(FluxusParser.ConstructorCallContext ctx) {
        List<Expression> args = ctx.expression().stream()
                .map(c -> (Expression) visit(c))
                .toList();
        return new Expression.ConstructorCall(
                createSpan(ctx),
                (Type) visit(ctx.typeIdentifier()),
                args
        );
    }

    @Override
    public Node visitLeftUnaryOperationExpression(FluxusParser.LeftUnaryOperationExpressionContext ctx) {
        Span span = createSpan(ctx);
        Expression.LeftUnaryOperator operator = switch (ctx.leftUnaryOperator().getText()) {
            case "-" -> Expression.LeftUnaryOperator.SUB;
            case "!" -> Expression.LeftUnaryOperator.NOT;
            case "#" -> Expression.LeftUnaryOperator.HASH;
            default -> throw new ParserException(span, "Unexpected value: " + ctx.leftUnaryOperator().getText());
        };
        Expression expression = (Expression) visit(ctx.expression());
        return new Expression.LeftUnary(
                span,
                operator,
                expression
        );
    }

    @Override
    public Node visitBinaryOperationExpression(FluxusParser.BinaryOperationExpressionContext ctx) {
        Span span = createSpan(ctx);
        Expression.BinaryOperator operator = switch (ctx.binaryOperator().getText()) {
            case "+" -> Expression.BinaryOperator.ADD;
            case "-" -> Expression.BinaryOperator.SUB;
            case "*" -> Expression.BinaryOperator.MUL;
            case "/" -> Expression.BinaryOperator.DIVIDE;
            case "==" -> Expression.BinaryOperator.EQ;
            case "!=" -> Expression.BinaryOperator.NEQ;
            case "<" -> Expression.BinaryOperator.LT;
            case "<=" -> Expression.BinaryOperator.LTE;
            case ">" -> Expression.BinaryOperator.GT;
            case ">=" -> Expression.BinaryOperator.GTE;
            case "&&" -> Expression.BinaryOperator.AND;
            case "||" -> Expression.BinaryOperator.OR;
            case "&" -> Expression.BinaryOperator.BIT_AND;
            case "|" -> Expression.BinaryOperator.BIT_OR;
            case "^" -> Expression.BinaryOperator.BIT_XOR;
            case "<<" -> Expression.BinaryOperator.SHIFT_LEFT;
            case ">>" -> Expression.BinaryOperator.SHIFT_RIGHT;
            default -> throw new ParserException(span, "Unexpected value: " + ctx.binaryOperator().getText());
        };
        Expression left = (Expression) visit(ctx.expression(0));
        Expression right = (Expression) visit(ctx.expression(1));
        return new Expression.Binary(
                span,
                left,
                operator,
                right
        );
    }

    @Override
    public Node visitParenExpression(FluxusParser.ParenExpressionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Node visitArrayCreation(FluxusParser.ArrayCreationContext ctx) {
        return new Expression.ArrayCreation(
                createSpan(ctx),
                (Expression) visit(ctx.expression(0)),
                (Expression) visit(ctx.expression(1))
        );
    }

    @Override
    public Node visitLiteralArrayCreation(FluxusParser.LiteralArrayCreationContext ctx) {
        List<Expression> expressions = ctx.expression().stream()
                .map(tree -> (Expression) visit(tree))
                .toList();
        return new Expression.ArrayLiteralCreation(
                createSpan(ctx),
                expressions
        );
    }

    @Override
    public Node visitNewExpression(FluxusParser.NewExpressionContext ctx) {
        return new Expression.New(
                createSpan(ctx),
                (Expression) visit(ctx.expression())
        );
    }

    @Override
    public Node visitCollectionAccess(FluxusParser.CollectionAccessContext ctx) {
        return new Expression.Binary(
                createSpan(ctx),
                (Expression) visit(ctx.expression(0)),
                Expression.BinaryOperator.INDEX,
                (Expression) visit(ctx.expression(1))
        );
    }

    @Override
    public Node visitTypeCastExpression(FluxusParser.TypeCastExpressionContext ctx) {
        return new Expression.TypeCast(
                createSpan(ctx),
                (Type) visit(ctx.typeIdentifier()),
                (Expression) visit(ctx.expression())
        );
    }

    @Override
    public Node visitITypeIdentifier(FluxusParser.ITypeIdentifierContext ctx) {
        return new Type.Trait.I(
                createSpan(ctx),
                Long.parseLong(ctx.SignedIntegerTypeIdentifier().getText().substring(1))
        );
    }

    @Override
    public Node visitUTypeIdentifier(FluxusParser.UTypeIdentifierContext ctx) {
        return new Type.Trait.U(
                createSpan(ctx),
                Long.parseLong(ctx.UnsignedIntegerTypeIdentifier().getText().substring(1))
        );
    }

    @Override
    public Node visitSimpleTypeIdentifier(FluxusParser.SimpleTypeIdentifierContext ctx) {
        return new Type.Simple(
                createSpan(ctx),
                ctx.getText()
        );
    }

    @Override
    public Node visitCollectionTypeIdentifier(FluxusParser.CollectionTypeIdentifierContext ctx) {
        return new Type.Trait.Collection(
                createSpan(ctx),
                (Type) visit(ctx.typeIdentifier(1)),
                (Type) visit(ctx.typeIdentifier(0))
        );
    }

    @Override
    public Node visitTypeDefinition(FluxusParser.TypeDefinitionContext ctx) {
        if (ctx.classTypeDefinition() != null)
            return visit(ctx.classTypeDefinition());
        throw new ParserException(createSpan(ctx), "Unknown type definition: " + ctx.getText());
    }

    @Override
    public Node visitClassTypeDefinition(FluxusParser.ClassTypeDefinitionContext ctx) {
        List<Statement.Import> imports = ctx.importStatement().stream()
                .map(tmCtx -> (Statement.Import) visit(tmCtx))
                .toList();
        List<TypeDefinition.ClassFieldDefinition> fields = ctx.fieldDefinition().stream()
                .map(cfdCtx -> (TypeDefinition.ClassFieldDefinition) visit(cfdCtx))
                .toList();
        List<TypeDefinition.ConstructorDefinition> constructors = ctx.constructorDefinition().stream()
                .map(cdCtx -> (TypeDefinition.ConstructorDefinition) visit(cdCtx))
                .toList();
        List<TypeDefinition.MethodDefinition> methods = ctx.methodDefinition().stream()
                .map(mdCtx -> (TypeDefinition.MethodDefinition) visit(mdCtx))
                .toList();
        return new TypeDefinition.ClassDefinition(
                createSpan(ctx),
                readDocComment(ctx.docComment()),
                imports,
                List.of(),
                fields,
                constructors,
                methods
        );
    }

    @Override
    public Node visitInitializedFieldDefinition(FluxusParser.InitializedFieldDefinitionContext ctx) {
        List<TypeDefinition.FieldModifier> fieldModifiers = ctx.fieldModifier().stream()
                .map(fmCtx -> (TypeDefinition.FieldModifier) visit(fmCtx))
                .toList();
        return new TypeDefinition.ClassFieldDefinition(
                createSpan(ctx),
                readDocComment(ctx.docComment()),
                fieldModifiers,
                (Type) visit(ctx.typeIdentifier()),
                ctx.SymbolIdentifier().getText(),
                Objects.requireNonNull((Expression) visit(ctx.expression()))
        );
    }

    @Override
    public Node visitFieldModifier(FluxusParser.FieldModifierContext ctx) {
        if (ctx.STATIC() != null)
            return new TypeDefinition.FieldModifier(createSpan(ctx), TypeDefinition.FieldModifierValue.STATIC);
        throw new ParserException(createSpan(ctx), "Unknown field modifier: " + ctx.getText());
    }

    @Override
    public Node visitConstructorDefinition(FluxusParser.ConstructorDefinitionContext ctx) {
        List<TypeDefinition.MethodModifier> modifiers = ctx.methodModifier().stream()
                .map(mpCtx -> (TypeDefinition.MethodModifier) visit(mpCtx))
                .toList();
        Statement body;
        if (ctx.statement() != null)
            body = (Statement) visit(ctx.statement());
        else
            body = (Statement) visit(ctx.statementList());
        return new TypeDefinition.ConstructorDefinition(
                createSpan(ctx),
                readDocComment(ctx.docComment()),
                modifiers,
                ctx.parametherList() == null
                        ? new TypeDefinition.MethodParameters(createSpan(ctx), Collections.emptyList())
                        : (TypeDefinition.MethodParameters) visit(ctx.parametherList()),
                body
        );
    }

    @Override
    public Node visitInstanceFieldAccess(FluxusParser.InstanceFieldAccessContext ctx) {
        return new Expression.InstanceFieldAccess(
                createSpan(ctx),
                (Expression) visit(ctx.expression()),
                ctx.SymbolIdentifier().getText()
        );
    }

    @Override
    public Node visitStaticFieldAccess(FluxusParser.StaticFieldAccessContext ctx) {
        return new Expression.StaticFieldAccess(
                createSpan(ctx),
                (Type) visit(ctx.typeIdentifier()),
                ctx.SymbolIdentifier().getText()
        );
    }

    @Override
    public Node visitImportStatement(FluxusParser.ImportStatementContext ctx) {
        return new Statement.Import(
                createSpan(ctx),
                ctx.FullyQualifiedTypeIdentifier().getText(),
                ctx.TypeIdentifier() == null ? null : ctx.TypeIdentifier().getText()
        );
    }

    @Override
    public Node visitMethodDefinition(FluxusParser.MethodDefinitionContext ctx) {
        List<TypeDefinition.MethodModifier> modifiers = ctx.methodModifier().stream()
                .map(mpCtx -> (TypeDefinition.MethodModifier) visit(mpCtx))
                .toList();
        Optional<Type> returnType = ctx.typeIdentifier() != null
                ? Optional.of((Type) visit(ctx.typeIdentifier()))
                : Optional.empty();
        Statement body;
        if (ctx.statement() != null)
            body = (Statement) visit(ctx.statement());
        else
            body = (Statement) visit(ctx.statementList());
        return new TypeDefinition.MethodDefinition(
                createSpan(ctx),
                readDocComment(ctx.docComment()),
                modifiers,
                returnType.orElse(null),
                ctx.SymbolIdentifier().getText(),
                ctx.parametherList() == null
                        ? new TypeDefinition.MethodParameters(createSpan(ctx), Collections.emptyList())
                        : (TypeDefinition.MethodParameters) visit(ctx.parametherList()),
                body
        );
    }

    @Override
    public Node visitMethodModifier(FluxusParser.MethodModifierContext ctx) {
        if (ctx.STATIC() != null)
            return new TypeDefinition.MethodModifier(createSpan(ctx), TypeDefinition.MethodModifierValue.STATIC);
        throw new ParserException(createSpan(ctx), "Unknown method modifier: " + ctx.getText());
    }

    @Override
    public Node visitParameter(FluxusParser.ParameterContext ctx) {
        return new TypeDefinition.MethodParameter(
                createSpan(ctx),
                (Type) visit(ctx.typeIdentifier()),
                ctx.SymbolIdentifier().getText()
        );
    }

    @Override
    public Node visitParametherList(FluxusParser.ParametherListContext ctx) {
        List<TypeDefinition.MethodParameter> parameters = ctx.parameter().stream()
                .map(pCtx -> (TypeDefinition.MethodParameter) visit(pCtx))
                .toList();
        return new TypeDefinition.MethodParameters(
                createSpan(ctx),
                parameters
        );
    }

    @Override
    public Node visitErrorNode(ErrorNode node) {
        Span span = createSpan((ParserRuleContext) node.getParent());
        throw new ParserException(span, node.getText());
    }

}
