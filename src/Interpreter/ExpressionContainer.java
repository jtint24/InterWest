package Interpreter;

import java.util.ArrayList;

public abstract class ExpressionContainer extends Expression {
    public abstract ArrayList<Expression> getContainedExpressions();
}
