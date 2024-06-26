package Interpreter;

import Elements.Type;
import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.Error;
import Parser.ParseTreeNode;
import Utils.Result;

import static ErrorManager.ErrorLibrary.getVariableNotFound;

public class VariableExpression extends Expression {
    String identifier;

    public VariableExpression(String identifier, ParseTreeNode underlyingParseTree) {
        this.identifier = identifier;
        this.underlyingParseTree = underlyingParseTree;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        return new ExpressionResult(situatedState, situatedState.get(identifier));
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        if (!context.hasVariable(identifier)) {
            context.addError(getVariableNotFound(this));
        }
        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return context.getVariableType(identifier);
    }
    @Override
    public Type getType(StaticReductionContext context) {
        return context.constantTypes.get(identifier);
    }

    @Override
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        staticValue = Result.error("Variable `"+identifier+"` can't be statically reduced");

        if (ValueLibrary.builtinValues.containsKey(identifier)) {
            staticValue = Result.ok(ValueLibrary.builtinValues.get(identifier));
        }

        if (context.declaredConstants.containsKey(identifier)) {
            staticValue = Result.ok(context.declaredConstants.get(identifier));
        }

        return context;
    }

    @Override
    public String toString() {
        return identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
