package Lexer;

public class BindingPowers {
    private final Associativity associativity;
    private final PrecedenceLevel precedenceLevel;

    public BindingPowers(PrecedenceLevel precedenceLevel, Associativity associativity) {
        this.precedenceLevel = precedenceLevel;
        this.associativity = associativity;
    }

    int leftBindingPower() {
        int leftPoint = associativity.ordinal();
        return (precedenceLevel.ordinal()*2) + leftPoint;
    }

    int rightBindingPower() {
        int rightPoint = 1-associativity.ordinal();
        return (precedenceLevel.ordinal()*2) + rightPoint;
    }
}
