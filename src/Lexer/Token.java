package Lexer;

public class Token {

    private final TokenValidator isCurrentlyValid;
    private final TokenValidator couldBeValid;
    private final String name;

    public Token(String name, TokenValidator isCurrentlyValid, TokenValidator couldBeValid) {
        this.isCurrentlyValid = isCurrentlyValid;
        this.couldBeValid = couldBeValid;
        this.name = name;
    }

    public boolean isCurrentlyValid(String s) {
        return isCurrentlyValid.isValid(s);
    }

    public boolean couldBeValid(String s) {
        return couldBeValid.isValid(s);
    }

    public interface TokenValidator {
        boolean isValid(String s);
    }

    @Override
    public String toString() {
        return name;
    }

    public static boolean rightBindsTighter(Token left, Token right) {
        int leftTightness;
        int rightTightness;

        if (right instanceof BinderToken) {
            rightTightness = ((BinderToken) right).leftBindingPower;
        } else {
            return false;
        }

        if (left instanceof BinderToken) {
            leftTightness = ((BinderToken) left).rightBindingPower;
        } else {
            return true;
        }

        return rightTightness > leftTightness;
    }

    public BinderToken toBinder(BindingPowers pl) {
        return new BinderToken(name, isCurrentlyValid, couldBeValid, pl);
    }
}
