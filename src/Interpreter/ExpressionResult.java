package Interpreter;

import Elements.Value;

public class ExpressionResult {
    public State resultingState;
    public Value resultingValue;

    public ExpressionResult(State resultingState, Value resultingValue) {
        this.resultingState = resultingState;
        this.resultingValue = resultingValue;
    }
}
