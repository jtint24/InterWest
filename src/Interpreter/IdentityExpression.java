package Interpreter;

import Elements.BuiltinValue;
import Elements.ExpressionFunction;
import Elements.Type;
import Elements.Value;
import ErrorManager.Error;
import Parser.ParseTreeNode;
import Utils.Result;

public class IdentityExpression extends Expression {
    Value wrappedValue;

    public IdentityExpression(Value wrappedValue, ParseTreeNode underlyingParseTree) {
        this.wrappedValue = wrappedValue;
        this.underlyingParseTree = underlyingParseTree;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        return new ExpressionResult(situatedState, wrappedValue);
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        if (wrappedValue instanceof ExpressionFunction) {
            context = ((ExpressionFunction) wrappedValue).validate(context);
            // context.addErrors(innerContext.errors);
        }
        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return wrappedValue.getType();
    }
    @Override
    public Type getType(StaticReductionContext context) {
        return wrappedValue.getType();
    }

    @Override
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        staticValue = Result.ok(wrappedValue);
        if (wrappedValue instanceof ExpressionFunction) {
            ((ExpressionFunction) wrappedValue).initializeStaticValues(context);
        }
        return context;
    }

    @Override
    public String toString() {
        return wrappedValue.toString();
    }
}
