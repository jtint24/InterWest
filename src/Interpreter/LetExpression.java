package Interpreter;

import Elements.Type;
import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.ErrorLibrary;
import Parser.ParseTreeNode;
import Utils.Result;

import java.util.HashMap;
import java.util.Map;

import static ErrorManager.ErrorLibrary.getRedefinition;

public class LetExpression extends Expression {
    String identifierName;
    public Expression exprToSet;
    boolean isForward;

    public LetExpression(String identifierName, Expression exprToSet, boolean isForward, ParseTreeNode underlyingParseTree) {
        this.identifierName = identifierName;
        this.exprToSet = exprToSet;
        this.underlyingParseTree = underlyingParseTree;
        this.isForward = isForward;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        ExpressionResult assignResult = exprToSet.evaluate(situatedState);

        State newState = assignResult.resultingState;
        Value valueToAssign = assignResult.resultingValue;

        newState.put(identifierName, valueToAssign);

        return new ExpressionResult(newState, ValueLibrary.trueValue);
    }

    @Override
    public ValidationContext validate(ValidationContext context) {

        if (!isForward) {
            if (exprToSet == null) {
                context.addError(ErrorLibrary.getEmptyLetError(this));
            } else {
                context = exprToSet.validate(context);
            }
            if (context.hasVariable(identifierName)) {
                context.addError(getRedefinition(this, identifierName));
            }
            if (exprToSet != null) {
                context.addVariableType(identifierName, exprToSet.getType(context));
            }
        } else {
            if (exprToSet != null) {
                context = exprToSet.validate(context);
            }
        }

        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return ValueLibrary.boolType;
    }

    @Override
    public Type getType(StaticReductionContext context) {
        return ValueLibrary.boolType;
    }

    @Override
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        staticValue = Result.ok(ValueLibrary.trueValue);

        if (exprToSet != null) {
            StaticReductionContext discardedContext = exprToSet.initializeStaticValues(context);

            // NOTE: We could build some mechanism to chain other errors to this one
            // Like, if this constant is used later, and the static value of it isn't available, then we could link it to
            // the error in the let statement instead of the error in the reference
            context.constantTypes.put(identifierName, exprToSet.getType(context));

            if (exprToSet.staticValue.isOK()) {
                context.declaredConstants.put(identifierName, exprToSet.staticValue.getOkValue());
            }
        }

        return context;
    }

    @Override
    public String toString() {
        String line = "let "+identifierName+" = "+exprToSet;
        if (!line.endsWith("\n")) {
            line += "\n";
        }
        return line;
    }

    @Override
    protected Map<String, Type> validateForwardDeclaration(ValidationContext context) {
        if (isForward) {
            if (exprToSet == null) {
                context.addError(ErrorLibrary.getEmptyLetError(this));
            }
            if (context.hasVariable(identifierName)) {
                context.addError(getRedefinition(this, identifierName));
            }
            return Map.of(identifierName, exprToSet.getType(context));
        } else {
            return new HashMap<>();
        }
    }
}
