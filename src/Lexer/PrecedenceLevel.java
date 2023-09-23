package Lexer;

public enum PrecedenceLevel {
    ASSIGNMENT, DISJUNCTION, CONJUNCTION, COMPARISON, ADDITION, MULTIPLICATION, BITWISE_SHIFT;

    private Associativity associativity;

    int leftBindingPower() {
        int leftPoint = associativity==Associativity.LEFT ? 0 : 1;
        return (this.ordinal()*2) + leftPoint;
    }

    int rightBindingPower() {
        int rightPoint = associativity==Associativity.RIGHT ? 0 : 1;
        return (this.ordinal()*2) + rightPoint;
    }
}
