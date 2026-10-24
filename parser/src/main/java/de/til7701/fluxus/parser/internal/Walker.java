package de.til7701.fluxus.parser.internal;

import de.til7701.fluxus.ast.Node;
import de.til7701.fluxus.ast.Script;
import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.expression.*;
import de.til7701.fluxus.ast.methods.*;
import de.til7701.fluxus.ast.statement.*;
import de.til7701.fluxus.ast.type.GenericType;
import de.til7701.fluxus.ast.type.SimpleType;
import de.til7701.fluxus.ast.type.Type;
import de.til7701.fluxus.ast.type.TypeList;
import de.til7701.fluxus.ast.type.trait.CollectionType;
import de.til7701.fluxus.ast.type.trait.IType;
import de.til7701.fluxus.ast.type.trait.UType;
import de.til7701.fluxus.ast.type_definition.TypeModifier;
import de.til7701.fluxus.ast.type_definition.TypeModifierValue;
import de.til7701.fluxus.ast.type_definition.classes.*;
import de.til7701.fluxus.parser.ParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@NullMarked
@RequiredArgsConstructor
public class Walker extends FluxusParserBaseVisitor<Node> {

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

    @Override
    public Node visitCompilationUnit(FluxusParser.CompilationUnitContext ctx) {
        if (ctx.typeDefinition() != null) {
            return visit(ctx.typeDefinition());
        } else {
            List<Statement> statements = Stream.concat(
                            ctx.importStatement().stream()
                                    .map(importContext -> (Import) visit(importContext)),
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
        return new StatementList(
                createSpan(ctx),
                ctx.statement().stream()
                        .map(tree -> (Statement) visit(tree))
                        .toList()
        );
    }

    @Override
    public Node visitVariableInitialization(FluxusParser.VariableInitializationContext ctx) {
        return new VariableInitialization(
                createSpan(ctx),
                ctx.SymbolIdentifier().getText(),
                (Expression) visit(ctx.expression())
        );
    }

    @Override
    public Node visitAssignment(FluxusParser.AssignmentContext ctx) {
        return new Assignment(
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
        return new WhenStatement(
                createSpan(ctx),
                ctx.DO() != null,
                (Expression) visit(ctx.expression()),
                body
        );
    }

    @Override
    public Node visitReturnStatement(FluxusParser.ReturnStatementContext ctx) {
        return new ReturnStatement(
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
        return new UnsignedIntegerLiteralExpression(
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
        return new SignedIntegerLiteralExpression(
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
        return new FloatLiteralExpression(
                createSpan(ctx),
                value,
                Long.parseLong(split[1])
        );
    }

    @Override
    public Node visitCharLiteralExpression(FluxusParser.CharLiteralExpressionContext ctx) {
        String text = ctx.CharLiteral().getText();
        return new CharLiteralExpression(
                createSpan(ctx),
                text.substring(1, text.length() - 1)
        );
    }

    @Override
    public Node visitStringLiteralExpression(FluxusParser.StringLiteralExpressionContext ctx) {
        String text = ctx.StringLiteral().getText();
        return new StringLiteralExpression(
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
        return new BooleanLiteralExpression(
                span,
                value
        );
    }

    @Override
    public Node visitSymbolIdentifierExpression(FluxusParser.SymbolIdentifierExpressionContext ctx) {
        return new SymbolExpression(
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
        return new StaticMethodCall(
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
        return new InstanceMethodCall(
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
        return new ConstructorCall(
                createSpan(ctx),
                (Type) visit(ctx.typeIdentifier()),
                args
        );
    }

    @Override
    public Node visitLeftUnaryOperationExpression(FluxusParser.LeftUnaryOperationExpressionContext ctx) {
        Span span = createSpan(ctx);
        LeftUnaryOperator operator = switch (ctx.leftUnaryOperator().getText()) {
            case "++" -> LeftUnaryOperator.INC;
            case "--" -> LeftUnaryOperator.DEC;
            case "-" -> LeftUnaryOperator.SUB;
            case "!" -> LeftUnaryOperator.NOT;
            case "#" -> LeftUnaryOperator.HASH;
            default -> throw new ParserException(span, "Unexpected value: " + ctx.leftUnaryOperator().getText());
        };
        Expression expression = (Expression) visit(ctx.expression());
        return new LeftUnaryExpression(
                span,
                operator,
                expression
        );
    }

    @Override
    public Node visitRightUnaryOperationExpression(FluxusParser.RightUnaryOperationExpressionContext ctx) {
        Span span = createSpan(ctx);
        RightUnaryOperator operator = switch (ctx.rightUnaryOperator().getText()) {
            case "++" -> RightUnaryOperator.INC;
            case "--" -> RightUnaryOperator.DEC;
            default -> throw new ParserException(span, "Unexpected value: " + ctx.rightUnaryOperator().getText());
        };
        Expression expression = (Expression) visit(ctx.expression());
        return new RightUnaryExpression(
                span,
                expression,
                operator
        );
    }

    @Override
    public Node visitBinaryOperationExpression(FluxusParser.BinaryOperationExpressionContext ctx) {
        Span span = createSpan(ctx);
        BinaryOperator operator = switch (ctx.binaryOperator().getText()) {
            case "+" -> BinaryOperator.ADD;
            case "-" -> BinaryOperator.SUB;
            case "*" -> BinaryOperator.MUL;
            case "/" -> BinaryOperator.DIVIDE;
            case "==" -> BinaryOperator.EQ;
            case "!=" -> BinaryOperator.NEQ;
            case "<" -> BinaryOperator.LT;
            case "<=" -> BinaryOperator.LTE;
            case ">" -> BinaryOperator.GT;
            case ">=" -> BinaryOperator.GTE;
            case "&&" -> BinaryOperator.AND;
            case "||" -> BinaryOperator.OR;
            case "&" -> BinaryOperator.BIT_AND;
            case "|" -> BinaryOperator.BIT_OR;
            case "^" -> BinaryOperator.BIT_XOR;
            case "<<" -> BinaryOperator.SHIFT_LEFT;
            case ">>" -> BinaryOperator.SHIFT_RIGHT;
            default -> throw new ParserException(span, "Unexpected value: " + ctx.binaryOperator().getText());
        };
        Expression left = (Expression) visit(ctx.expression(0));
        Expression right = (Expression) visit(ctx.expression(1));
        return new BinaryExpression(
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
        return new ArrayCreation(
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
        return new ArrayLiteralCreation(
                createSpan(ctx),
                expressions
        );
    }

    @Override
    public Node visitNewExpression(FluxusParser.NewExpressionContext ctx) {
        return new NewExpression(
                createSpan(ctx),
                (Expression) visit(ctx.expression())
        );
    }

    @Override
    public Node visitCollectionAccess(FluxusParser.CollectionAccessContext ctx) {
        return new BinaryExpression(
                createSpan(ctx),
                (Expression) visit(ctx.expression(0)),
                BinaryOperator.INDEX,
                (Expression) visit(ctx.expression(1))
        );
    }

    @Override
    public Node visitTypeCastExpression(FluxusParser.TypeCastExpressionContext ctx) {
        return new TypeCastExpression(
                createSpan(ctx),
                (Type) visit(ctx.typeIdentifier()),
                (Expression) visit(ctx.expression())
        );
    }

    @Override
    public Node visitITypeIdentifier(FluxusParser.ITypeIdentifierContext ctx) {
        return new IType(
                createSpan(ctx),
                Long.parseLong(ctx.SignedIntegerTypeIdentifier().getText().substring(1))
        );
    }

    @Override
    public Node visitUTypeIdentifier(FluxusParser.UTypeIdentifierContext ctx) {
        return new UType(
                createSpan(ctx),
                Long.parseLong(ctx.UnsignedIntegerTypeIdentifier().getText().substring(1))
        );
    }

    @Override
    public Node visitSimpleTypeIdentifier(FluxusParser.SimpleTypeIdentifierContext ctx) {
        return new SimpleType(
                createSpan(ctx),
                ctx.getText()
        );
    }

    @Override
    public Node visitCollectionTypeIdentifier(FluxusParser.CollectionTypeIdentifierContext ctx) {
        return new CollectionType(
                createSpan(ctx),
                (Type) visit(ctx.typeIdentifier(1)),
                (Type) visit(ctx.typeIdentifier(0))
        );
    }

    @Override
    public Node visitGenericTypeIdentifier(FluxusParser.GenericTypeIdentifierContext ctx) {
        return new GenericType(
                createSpan(ctx),
                (Type) visit(ctx.typeIdentifier()),
                (TypeList) visitGenericTypeList(ctx.genericTypeList())
        );
    }

    public List<Type> constructGenericTypeList(FluxusParser.GenericTypeListContext ctx) {
        if (ctx == null || ctx.typeIdentifier().isEmpty())
            return Collections.emptyList();
        List<Type> typeArguments = new ArrayList<>();
        for (int i = 1; i < ctx.typeIdentifier().size(); i++)
            typeArguments.add((Type) visit(ctx.typeIdentifier(i)));
        return Collections.unmodifiableList(typeArguments);
    }

    @Override
    public Node visitGenericTypeList(FluxusParser.GenericTypeListContext ctx) {
        List<Type> typeArguments = constructGenericTypeList(ctx);
        return new TypeList(
                createSpan(ctx),
                typeArguments
        );
    }

    @Override
    public Node visitTypeDefinition(FluxusParser.TypeDefinitionContext ctx) {
        if (ctx.classTypeDefinition() != null)
            return visit(ctx.classTypeDefinition());
        throw new ParserException(createSpan(ctx), "Unknown type definition: " + ctx.getText());
    }

    @Override
    public Node visitTypeModifier(FluxusParser.TypeModifierContext ctx) {
        if (ctx.PUB() != null)
            return new TypeModifier(createSpan(ctx), TypeModifierValue.PUB);
        throw new ParserException(createSpan(ctx), "Unknown type modifier: " + ctx.getText());
    }

    @Override
    public Node visitClassTypeDefinition(FluxusParser.ClassTypeDefinitionContext ctx) {
        List<Import> imports = ctx.importStatement().stream()
                .map(tmCtx -> (Import) visit(tmCtx))
                .toList();
        List<TypeModifier> typeModifiers = ctx.typeModifier().stream()
                .map(tmCtx -> (TypeModifier) visit(tmCtx))
                .toList();
        List<ClassFieldDefinition> fields = ctx.fieldDefinition().stream()
                .map(cfdCtx -> (ClassFieldDefinition) visit(cfdCtx))
                .toList();
        List<ConstructorDefinition> constructors = ctx.constructorDefinition().stream()
                .map(cdCtx -> (ConstructorDefinition) visit(cdCtx))
                .toList();
        List<MethodDefinition> methods = ctx.methodDefinition().stream()
                .map(mdCtx -> (MethodDefinition) visit(mdCtx))
                .toList();
        return new ClassDefinition(
                createSpan(ctx),
                imports,
                typeModifiers,
                new TypeList(
                        ctx.genericTypeList() == null ?
                                Span.undefined() :
                                createSpan(ctx.genericTypeList()),
                        constructGenericTypeList(ctx.genericTypeList())
                ),
                new TypeList(
                        Span.undefined(),
                        List.of()
                ),
                fields,
                constructors,
                methods
        );
    }

    @Override
    public Node visitInitializedFieldDefinition(FluxusParser.InitializedFieldDefinitionContext ctx) {
        List<FieldModifier> fieldModifiers = ctx.fieldModifier().stream()
                .map(fmCtx -> (FieldModifier) visit(fmCtx))
                .toList();
        return new ClassFieldDefinition(
                createSpan(ctx),
                fieldModifiers,
                (Type) visit(ctx.typeIdentifier()),
                ctx.SymbolIdentifier().getText(),
                Objects.requireNonNull((Expression) visit(ctx.expression()))
        );
    }

    @Override
    public Node visitFieldModifier(FluxusParser.FieldModifierContext ctx) {
        if (ctx.STATIC() != null)
            return new FieldModifier(createSpan(ctx), FieldModifierValue.STATIC);
        throw new ParserException(createSpan(ctx), "Unknown field modifier: " + ctx.getText());
    }

    @Override
    public Node visitConstructorDefinition(FluxusParser.ConstructorDefinitionContext ctx) {
        List<MethodModifier> modifiers = ctx.methodModifier().stream()
                .map(mpCtx -> (MethodModifier) visit(mpCtx))
                .toList();
        Statement body;
        if (ctx.statement() != null)
            body = (Statement) visit(ctx.statement());
        else
            body = (Statement) visit(ctx.statementList());
        return new ConstructorDefinition(
                createSpan(ctx),
                modifiers,
                ctx.parametherList() == null
                        ? new MethodParameters(createSpan(ctx), Collections.emptyList())
                        : (MethodParameters) visit(ctx.parametherList()),
                body
        );
    }

    @Override
    public Node visitInstanceFieldAccess(FluxusParser.InstanceFieldAccessContext ctx) {
        return new InstanceFieldAccess(
                createSpan(ctx),
                (Expression) visit(ctx.expression()),
                ctx.SymbolIdentifier().getText()
        );
    }

    @Override
    public Node visitStaticFieldAccess(FluxusParser.StaticFieldAccessContext ctx) {
        return new StaticFieldAccess(
                createSpan(ctx),
                (Type) visit(ctx.typeIdentifier()),
                ctx.SymbolIdentifier().getText()
        );
    }

    @Override
    public Node visitImportStatement(FluxusParser.ImportStatementContext ctx) {
        return new Import(
                createSpan(ctx),
                ctx.FullyQualifiedTypeIdentifier().getText(),
                ctx.TypeIdentifier() == null ? null : ctx.TypeIdentifier().getText()
        );
    }

    @Override
    public Node visitMethodDefinition(FluxusParser.MethodDefinitionContext ctx) {
        List<MethodModifier> modifiers = ctx.methodModifier().stream()
                .map(mpCtx -> (MethodModifier) visit(mpCtx))
                .toList();
        Optional<Type> returnType = ctx.typeIdentifier() != null
                ? Optional.of((Type) visit(ctx.typeIdentifier()))
                : Optional.empty();
        Statement body;
        if (ctx.statement() != null)
            body = (Statement) visit(ctx.statement());
        else
            body = (Statement) visit(ctx.statementList());
        return new MethodDefinition(
                createSpan(ctx),
                modifiers,
                returnType,
                ctx.SymbolIdentifier().getText(),
                ctx.parametherList() == null
                        ? new MethodParameters(createSpan(ctx), Collections.emptyList())
                        : (MethodParameters) visit(ctx.parametherList()),
                body
        );
    }

    @Override
    public Node visitMethodModifier(FluxusParser.MethodModifierContext ctx) {
        if (ctx.STATIC() != null)
            return new MethodModifier(createSpan(ctx), MethodModifierValue.STATIC);
        if (ctx.PUB() != null)
            return new MethodModifier(createSpan(ctx), MethodModifierValue.PUB);
        throw new ParserException(createSpan(ctx), "Unknown method modifier: " + ctx.getText());
    }

    @Override
    public Node visitParameter(FluxusParser.ParameterContext ctx) {
        return new MethodParameter(
                createSpan(ctx),
                (Type) visit(ctx.typeIdentifier()),
                ctx.SymbolIdentifier().getText()
        );
    }

    @Override
    public Node visitParametherList(FluxusParser.ParametherListContext ctx) {
        List<MethodParameter> parameters = ctx.parameter().stream()
                .map(pCtx -> (MethodParameter) visit(pCtx))
                .toList();
        return new MethodParameters(
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
