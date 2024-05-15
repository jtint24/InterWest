package Interpreter;

import Elements.*;
import ErrorManager.ErrorManager;
import ErrorManager.Error;
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

    public Expression buildExpression(NonterminalParseTreeNode ptNode) {
        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);

        // System.out.println(ptNode.getKind());

        if (ptNode.getKind().toString().equals("TreeKind(full expression)")) {
            ptNode = (NonterminalParseTreeNode) ptNode.getChildren().get(0);
        }

        if (ptNode.getKind().toString().equals("TreeKind(delimited expression)")) {
            if (ptNode.getChildren().size() == 0) {
                return null;
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
            default -> {
                throw new RuntimeException("Unknown terminal type `"+tokenName+"`");
            }
        };
    }

    private Expression buildExpressionCall(NonterminalParseTreeNode ptNode) {
        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);

        // [called-expr] [(] [arg list] [)]

        Expression calledExpr = buildExpression((NonterminalParseTreeNode) ptNode.getChildren().get(0));

        NonterminalParseTreeNode argList = (NonterminalParseTreeNode) ptNode.getChildren().get(2);
        ArrayList<Expression> argumentExpressions = new ArrayList<>();

        for (ParseTreeNode argument : argList.getChildren()) {
            argumentExpressions.add(buildExpression((NonterminalParseTreeNode) argument));
        }

        return new FunctionExpression(calledExpr, argumentExpressions, ptNode);
    }

    public Expression buildIfExpression(NonterminalParseTreeNode ptNode) {
        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);

        // [if] [condition] [{] [statements...] [}]

        Expression condition = buildExpression((NonterminalParseTreeNode) ptNode.getChildren().get(1));

        ArrayList<Expression> childExpressions = new ArrayList<>();

        for (int i = 3; i<ptNode.getChildren().size()-1; i++) {
            if (ptNode.getChildren().get(i) instanceof NonterminalParseTreeNode) {
                Expression builtExpression = buildExpression((NonterminalParseTreeNode) ptNode.getChildren().get(i));
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
                childExpressions.add(buildExpression((NonterminalParseTreeNode) childNode));
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
        Expression assignToExpression = buildExpression((NonterminalParseTreeNode) ptNode.getChildren().get(3));

        return new LetExpression(identifier, assignToExpression, ptNode);
    };

    public Expression buildReturnExpression(NonterminalParseTreeNode ptNode) {
        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);
        // [return] [expression]

        Expression returnToExpression = buildExpression((NonterminalParseTreeNode) ptNode.getChildren().get(1));

        return new ReturnExpression(returnToExpression, ptNode);
    }

    public Expression buildLambdaExpression(NonterminalParseTreeNode ptNode) {
        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);

        // [{] [parameterList?] [->] [type] [expression] [}]

        List<ParseTreeNode> parameterList;
        int subExpressionsStartIdx;

        if (ptNode.getChildren().get(1) instanceof NonterminalParseTreeNode) {
            parameterList = ((NonterminalParseTreeNode) ptNode.getChildren().get(1)).getChildren();
            subExpressionsStartIdx = 4;
        } else {
            parameterList = new ArrayList<>();
            subExpressionsStartIdx = 3;
        }

        Expression resultTypeExpression = buildExpression( (NonterminalParseTreeNode) ptNode.getChildren().get(subExpressionsStartIdx-1));

        ArrayList<Expression> subExpressions = new ArrayList<>();
        for (int i = subExpressionsStartIdx; i<ptNode.getChildren().size()-1; i++) {
            subExpressions.add(buildExpression( (NonterminalParseTreeNode) ptNode.getChildren().get(i)));
        }

        ArrayList<String> paramNames = new ArrayList<>();
        ArrayList<Type> paramTypes = new ArrayList<>();

        for (int i = 0; i<parameterList.size(); i+=2) {
            Expression paramType = buildExpression((NonterminalParseTreeNode) parameterList.get(i));
            String paramName = ((TerminalParseTreeNode)parameterList.get(i+1)).getWrappedSymbol().getLexeme();

            paramNames.add(paramName);
            paramTypes.add(new TypeExpression(paramType));
        }

        // System.out.println(Arrays.toString(paramNames.toArray()));
        // System.out.println(Arrays.toString(paramTypes.toArray()));

        Type returnType = new TypeExpression(resultTypeExpression);

        FunctionType type = new FunctionType(returnType, paramTypes.toArray(new Type[0]));

        Function lambdaFunc = new ExpressionFunction(type, new ReturnableExpressionSeries(returnType, subExpressions),  paramNames.toArray(new String[0]));

        return new IdentityExpression(lambdaFunc, ptNode);

        // TODO: Add func types that take type expressions that have to be resolved
    }


}
