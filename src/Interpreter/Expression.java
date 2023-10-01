package Interpreter;

import Elements.Value;

public abstract class Expression extends Value {
    public abstract ExpressionResult evaluate(State situatedState);
}
