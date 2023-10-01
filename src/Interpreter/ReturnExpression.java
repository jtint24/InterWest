package Interpreter;

public class ReturnExpression extends Expression {

    Expression exprToReturn;

    public ReturnExpression(Expression exprToReturn) {
        this.exprToReturn = exprToReturn;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        return exprToReturn.evaluate(situatedState);
    }

    @Override
    public String toString() {
        return "return "+exprToReturn+"\n";
    }
}
