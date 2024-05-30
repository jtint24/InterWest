package Interpreter;

import Elements.*;
import ErrorManager.ErrorManager;
import Lexer.TokenLibrary;
import Parser.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpressionBuilder {
    ErrorManager errorManager;

    ExpressionBuilder(ErrorManager errorManager) {
        this.errorManager = errorManager;
    }

    public Expression buildExpression(ParseTreeNode ptNode) {
        if (ptNode instanceof NonterminalParseTreeNode) {
            return buildNonterminalExpression((NonterminalParseTreeNode) ptNode);
        } else if (ptNode instanceof TerminalParseTreeNode) {
            return buildTerminalExpression((TerminalParseTreeNode) ptNode);
        }
        throw new RuntimeException("This PTNode is neither terminal nor nonterminal???");
    }

    public Expression buildNonterminalExpression(NonterminalParseTreeNode ptNode) {
        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);

        // System.out.println(ptNode.getKind());

        if (ptNode.getKind().toString().equals("TreeKind(full expression)")) {
            ptNode = (NonterminalParseTreeNode) ptNode.getChildren().get(0);
        }

        if (ptNode.getKind().toString().equals("TreeKind(delimited expression)")) {
            if (ptNode.getChildren().size() == 0) {
                Expression expr = new NoOpExpression(ptNode);
                expr.underlyingParseTree = ptNode;
                return expr;
                // return null;
            }

            // Gets expressions wrapped in parentheses
            if (ptNode.getChildren().size() == 3
                    && ptNode.getChildren().get(0) instanceof TerminalParseTreeNode
                    && ptNode.getChildren().get(2) instanceof TerminalParseTreeNode
            ) {
                return buildExpression(ptNode.getChildren().get(1));
            }

            ParseTreeNode childNode = ptNode.getChildren().get(0);
            if (childNode instanceof NonterminalParseTreeNode) {
                ptNode = (NonterminalParseTreeNode) childNode;
            } else {
                Expression expr = buildTerminalExpression((TerminalParseTreeNode) childNode);
                expr.underlyingParseTree = ptNode;
                return expr;
            }
        }

        Expression expr = switch (ptNode.getKind().toString()) {
            case "TreeKind(file)" -> buildFileExpression(ptNode);
            case "TreeKind(let)" -> buildLetExpression(ptNode);
            case "TreeKind(return)" -> buildReturnExpression(ptNode);
            case "TreeKind(lambda)" -> buildLambdaExpression(ptNode);
            case "TreeKind(if statement)" -> buildIfExpression(ptNode);
            case "TreeKind(expression call)" -> buildExpressionCall(ptNode);
            case "TreeKind(binary expression)" -> buildBinaryExpression(ptNode);
            case "TreeKind(type declaration)" -> buildTypeDeclaration(ptNode);

            default -> {
                throw new RuntimeException("Unknown nonterminal type `"+ptNode.getKind()+"`");
            }
        };

        expr.underlyingParseTree = ptNode;
        return expr;
    }


    public Expression buildTerminalExpression(TerminalParseTreeNode ptNode) {
        String tokenName = ptNode.getWrappedSymbol().getTokenType().toString();
        String lexeme = ptNode.getWrappedSymbol().getLexeme();

        return switch (tokenName) {
            case "identifier" -> new VariableExpression(lexeme, ptNode);
            case "int" -> new IdentityExpression(new ValueWrapper<>(Integer.parseInt(lexeme), ValueLibrary.intType), ptNode);
            case "!=" -> new IdentityExpression(ValueLibrary.unequalsFunc, ptNode);
            default -> {
                throw new RuntimeException("Unknown terminal type `"+tokenName+"`");
            }
        };
    }

    private Expression buildTypeDeclaration(NonterminalParseTreeNode ptNode) {
        Expression internalExpression = buildExpression(ptNode.getChildren().get(1));

        return new RefinementTypeExpression(internalExpression, ptNode);
    }

    private Expression buildExpressionCall(NonterminalParseTreeNode ptNode) {
        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);

        // [called-expr] [(] [arg list] [)]

        Expression calledExpr = buildNonterminalExpression((NonterminalParseTreeNode) ptNode.getChildren().get(0));

        NonterminalParseTreeNode argList = (NonterminalParseTreeNode) ptNode.getChildren().get(2);
        ArrayList<Expression> argumentExpressions = new ArrayList<>();

        for (ParseTreeNode argument : argList.getChildren()) {
            argumentExpressions.add(buildNonterminalExpression((NonterminalParseTreeNode) argument));
        }

        return new FunctionExpression(calledExpr, argumentExpressions, ptNode);
    }

    private Expression buildBinaryExpression(NonterminalParseTreeNode ptNode) {
        // [lhs] [operator] [rhs]

        Expression lhs = buildExpression(ptNode.getChildren().get(0));
        Expression rhs = buildExpression(ptNode.getChildren().get(2));
        Expression operator = buildExpression(ptNode.getChildren().get(1));

        ArrayList<Expression> arguments = new ArrayList<>() {{
            add(lhs);
            add(rhs);
        }};

        return new FunctionExpression(operator, arguments, ptNode);
    }

    public Expression buildIfExpression(NonterminalParseTreeNode ptNode) {
        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);

        // [if] [condition] [{] [statements...] [}]

        Expression condition = buildNonterminalExpression((NonterminalParseTreeNode) ptNode.getChildren().get(1));

        ArrayList<Expression> childExpressions = new ArrayList<>();

        for (int i = 3; i<ptNode.getChildren().size()-1; i++) {
            if (ptNode.getChildren().get(i) instanceof NonterminalParseTreeNode) {
                Expression builtExpression = buildNonterminalExpression((NonterminalParseTreeNode) ptNode.getChildren().get(i));
                if (builtExpression != null) {
                    childExpressions.add(builtExpression);
                }
            }
        }

        ExpressionSeries internalSeries = new ExpressionSeries(childExpressions);


        return new ConditionalExpression(internalSeries, condition, ptNode);
    }

    public Expression buildFileExpression(NonterminalParseTreeNode ptNode) {
        // System.out.println(ptNode.getKind());

        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);
        ArrayList<Expression> childExpressions = new ArrayList<>();
        for (ParseTreeNode childNode : ptNode.getChildren()) {
            if (childNode instanceof NonterminalParseTreeNode) {
                childExpressions.add(buildNonterminalExpression((NonterminalParseTreeNode) childNode));
            } else {
                throw new RuntimeException("Expected nonterminal node, got terminal");
            }
        }
        return new ExpressionSeries(childExpressions);
    };
    public Expression buildLetExpression(NonterminalParseTreeNode ptNode) {
        // System.out.println(ptNode.getKind());

        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);
        // [let] [identifier] [=] [expression]
        String identifier = ((TerminalParseTreeNode)ptNode.getChildren().get(1)).getWrappedSymbol().getLexeme();
        Expression assignToExpression = buildNonterminalExpression((NonterminalParseTreeNode) ptNode.getChildren().get(3));

        return new LetExpression(identifier, assignToExpression, ptNode);
    };

    public Expression buildReturnExpression(NonterminalParseTreeNode ptNode) {
        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);
        // [return] [expression]

        Expression returnToExpression = buildNonterminalExpression((NonterminalParseTreeNode) ptNode.getChildren().get(1));

        return new ReturnExpression(returnToExpression, ptNode);
    }

    public Expression buildLambdaExpression(NonterminalParseTreeNode ptNode) {

        // [regular?] [{] [parameterList?] [->] [expression...] [}]

        // System.out.println(Arrays.toString(ptNode.getChildren().stream().map(a -> a.getSymbols()).toArray()));
        List<ParseTreeNode> parameterList;
        int subExpressionsStartIdx;
        int lBraceIdx = 0;
        boolean isRegular = false;

        if (ptNode.getChildren().get(0) instanceof TerminalParseTreeNode && ((TerminalParseTreeNode) ptNode.getChildren().get(0)).getWrappedSymbol().getLexeme().equals("regular")) {
            lBraceIdx = 1;
            isRegular = true;
        }

        if (ptNode.getChildren().get(1+lBraceIdx) instanceof NonterminalParseTreeNode) {
            parameterList = ((NonterminalParseTreeNode) ptNode.getChildren().get(1+lBraceIdx)).getChildren();
            subExpressionsStartIdx = 3+lBraceIdx;
        } else {
            parameterList = new ArrayList<>();
            subExpressionsStartIdx = 2+lBraceIdx;
        }

        ArrayList<Expression> subExpressions = new ArrayList<>();
        Type returnType;

        if (ptNode.getChildren().size()-subExpressionsStartIdx == 2) {
            // We have a single-expression lambda
            subExpressions.add(buildNonterminalExpression((NonterminalParseTreeNode) ptNode.getChildren().get(ptNode.getChildren().size() - 2)));
            returnType = null;
            // System.out.println("ReturnType: "+returnType);
            // System.out.println("SubExpression(0): "+subExpressions.get(0));

        } else {

            Expression resultTypeExpression = buildExpression(ptNode.getChildren().get(subExpressionsStartIdx));

            returnType = new TypeExpression(resultTypeExpression);

            for (int i = subExpressionsStartIdx+1; i < ptNode.getChildren().size() - 1; i++) {
                subExpressions.add(buildNonterminalExpression((NonterminalParseTreeNode) ptNode.getChildren().get(i)));
            }
        }

        if (subExpressions.size() == 1 && !(subExpressions.get(0) instanceof ReturnExpression)) {
            subExpressions.set(0, new ReturnExpression(subExpressions.get(0), subExpressions.get(0).underlyingParseTree));
        }

        ArrayList<String> paramNames = new ArrayList<>();
        ArrayList<Type> paramTypes = new ArrayList<>();

        for (int i = 0; i<parameterList.size(); i+=2) {
            Expression paramType = buildNonterminalExpression((NonterminalParseTreeNode) parameterList.get(i));
            String paramName = ((TerminalParseTreeNode)parameterList.get(i+1)).getWrappedSymbol().getLexeme();

            paramNames.add(paramName);
            paramTypes.add(new TypeExpression(paramType));
        }

        // System.out.println(Arrays.toString(paramNames.toArray()));
        // System.out.println(Arrays.toString(paramTypes.toArray()));

        FunctionType type = new FunctionType(returnType, paramTypes.toArray(new Type[0]));

        Function lambdaFunc = new ExpressionFunction(type, new ReturnableExpressionSeries(returnType, subExpressions),  paramNames.toArray(new String[0]), isRegular, ptNode);

        return new IdentityExpression(lambdaFunc, ptNode);

        // TODO: Add func types that take type expressions that have to be resolved
    }


}
