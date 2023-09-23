package Lexer;

public class BinderToken extends Token{
    int leftBindingPower;
    int rightBindingPower;
    public BinderToken(String name, TokenValidator isCurrentlyValid, TokenValidator couldBeValid, int leftBindingPower, int rightBindingPower) {
        super(name, isCurrentlyValid, couldBeValid);
    }

    public BinderToken(String name, TokenValidator isCurrentlyValid, TokenValidator couldBeValid, PrecedenceLevel precedenceLevel) {
        super(name, isCurrentlyValid, couldBeValid);
    }
}
