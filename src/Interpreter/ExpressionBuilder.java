package Interpreter;

import Elements.ExpressionFunction;
import Elements.FunctionType;
import Elements.ValueLibrary;
import Elements.ValueWrapper;
import ErrorManager.ErrorManager;
import ErrorManager.Error;
import Lexer.TokenLibrary;
import Parser.NonterminalLibrary;
import Parser.NonterminalParseTreeNode;
import Parser.ParseTreeNode;
import Parser.TerminalParseTreeNode;

import java.util.ArrayList;
import java.util.Arrays;

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
            ParseTreeNode childNode = ptNode.getChildren().get(0);
            if (childNode instanceof NonterminalParseTreeNode) {
                ptNode = (NonterminalParseTreeNode) childNode;
            } else {
                return buildTerminalExpression((TerminalParseTreeNode) childNode);
            }
        }

        return switch (ptNode.getKind().toString()) {
            case "TreeKind(file)" -> buildFileExpression(ptNode);
            case "TreeKind(let)" -> buildLetExpression(ptNode);
            case "TreeKind(return)" -> buildReturnExpression(ptNode);
            case "TreeKind(lambda)" -> buildLambdaExpression(ptNode);
            case "TreeKind(if statement)" -> buildIfExpression(ptNode);
            default -> {
                errorManager.logError(new Error(Error.ErrorType.INTERPRETER_ERROR, "Unknown nonterminal type `"+ptNode.getKind()+"`", true));
                yield null;
            }
        };
    }

    public Expression buildTerminalExpression(TerminalParseTreeNode ptNode) {
        String tokenName = ptNode.getWrappedSymbol().getTokenType().toString();
        String lexeme = ptNode.getWrappedSymbol().getLexeme();

        return switch (tokenName) {
            case "identifier" -> new VariableExpression(lexeme);
            case "int" -> new IdentityExpression(new ValueWrapper<>(Integer.parseInt(lexeme), ValueLibrary.intType));
            default -> {
                (new RuntimeException()).printStackTrace();
                errorManager.logError(new Error(Error.ErrorType.INTERPRETER_ERROR, "Unknown terminal type `"+tokenName+"`", true));
                yield null;
            }
        };
    }

    public Expression buildIfExpression(NonterminalParseTreeNode ptNode) {
        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);

        // [if] [condition] [{] [statements...] [}]

        Expression condition = buildExpression((NonterminalParseTreeNode) ptNode.getChildren().get(1));

        ArrayList<Expression> childExpressions = new ArrayList<>();

        for (int i = 3; i<ptNode.getChildren().size()-1; i++) {
            if (ptNode.getChildren().get(i) instanceof NonterminalParseTreeNode) {
                childExpressions.add(buildExpression((NonterminalParseTreeNode) ptNode.getChildren().get(i)));
            }
        }

        ExpressionSeries internalSeries = new ExpressionSeries(childExpressions);


        return new ConditionalExpression(internalSeries, condition);
    }

    public Expression buildFileExpression(NonterminalParseTreeNode ptNode) {
        // System.out.println(ptNode.getKind());

        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);
        ArrayList<Expression> childExpressions = new ArrayList<>();
        for (ParseTreeNode childNode : ptNode.getChildren()) {
            if (childNode instanceof NonterminalParseTreeNode) {
                childExpressions.add(buildExpression((NonterminalParseTreeNode) childNode));
            } else {
                errorManager.logError(new Error(Error.ErrorType.INTERPRETER_ERROR, "Expected nonterminal node, got terminal", true));
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

        return new LetExpression(identifier, assignToExpression);
    };

    public Expression buildReturnExpression(NonterminalParseTreeNode ptNode) {
        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);
        // [return] [expression]

        Expression returnToExpression = buildExpression((NonterminalParseTreeNode) ptNode.getChildren().get(1));

        return new ReturnExpression(returnToExpression);
    }

    public Expression buildLambdaExpression(NonterminalParseTreeNode ptNode) {
        ptNode.removeSymbolsOfType(TokenLibrary.whitespace);

        // [{] [parameterList] [->] [expression] [}]

        NonterminalParseTreeNode parameterList = (NonterminalParseTreeNode) ptNode.getChildren().get(1);

        Expression resultingExpression = buildExpression((NonterminalParseTreeNode) ptNode.getChildren().get(3));

        ArrayList<String> paramNames = new ArrayList<>();
        ArrayList<Expression> paramTypes = new ArrayList<>();

        for (int i = 0; i<parameterList.getChildren().size(); i+=2) {
            Expression paramType = buildExpression((NonterminalParseTreeNode) parameterList.getChildren().get(i));
            String paramName = ((TerminalParseTreeNode)parameterList.getChildren().get(i+1)).getWrappedSymbol().getLexeme();

            paramNames.add(paramName);
            paramTypes.add(paramType);
        }

        System.out.println(Arrays.toString(paramNames.toArray()));
        System.out.println(Arrays.toString(paramTypes.toArray()));

        return null;
        // return new IdentityExpression(new ExpressionFunction(new FunctionType(), resultingExpression));//TODO: Add func types that take type expressions that have to be resolved
    }


}
