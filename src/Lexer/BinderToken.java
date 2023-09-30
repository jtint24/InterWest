package Lexer;

public class BinderToken extends Token {
    public int leftBindingPower;
    public int rightBindingPower;

    public BinderToken(String name, TokenValidator isCurrentlyValid, TokenValidator couldBeValid, BindingPowers precedenceLevel) {
        super(name, isCurrentlyValid, couldBeValid);
        leftBindingPower = precedenceLevel.leftBindingPower();
        rightBindingPower = precedenceLevel.rightBindingPower();
    }
}
