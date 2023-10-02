package Interpreter;

import Elements.Value;

public class ExpressionResult {
    public State resultingState;
    public Value resultingValue;

    private boolean earlyReturn = false;

    public ExpressionResult(State resultingState, Value resultingValue) {
        this.resultingState = resultingState;
        this.resultingValue = resultingValue;
    }

    public ExpressionResult(State resultingState, Value resultingValue, boolean earlyReturn) {
        this.resultingState = resultingState;
        this.resultingValue = resultingValue;
        this.earlyReturn = earlyReturn;
    }

    public boolean getEarlyReturn() {
        return earlyReturn;
    }

    public void setEarlyReturn(boolean a) { earlyReturn = a; }
}
