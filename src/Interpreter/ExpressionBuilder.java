package Interpreter;

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

public class ExpressionBuilder {
    ErrorManager errorManager;

    ExpressionBuilder(ErrorManager errorManager) {
        this.errorManager = errorManager;
    }

    public Expression buildExpression(NonterminalParseTreeNode ptNode) {

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
            case "int" -> new IdentityExpression(new ValueWrapper<Integer>(Integer.parseInt(lexeme), ValueLibrary.intType));
            default -> {
                errorManager.logError(new Error(Error.ErrorType.INTERPRETER_ERROR, "Unknown terminal type `"+tokenName+"`", true));
                yield null;
            }
        };
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


}
