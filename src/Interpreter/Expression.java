package Interpreter;

public abstract class Expression {
    public abstract ExpressionResult evaluate(State situatedState);
}
