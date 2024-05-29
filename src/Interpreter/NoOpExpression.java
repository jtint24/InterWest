package Interpreter;

import Elements.Type;
import Elements.ValueLibrary;
import Parser.NonterminalParseTreeNode;
import Parser.ParseTreeNode;

public class NoOpExpression extends Expression {
    ParseTreeNode ptNode;
    public NoOpExpression(NonterminalParseTreeNode ptNode) {
        this.ptNode = ptNode;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        return new ExpressionResult(situatedState, null);
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return ValueLibrary.voidType;
    }

    @Override
    public Type getType(StaticReductionContext context) {
        return ValueLibrary.voidType;
    }

    @Override
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        return context;
    }

    @Override
    public String toString() {
        return "(noop)";
    }
}
