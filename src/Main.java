import ErrorManager.ErrorManager;
import InputBuffer.InputBuffer;
import Lexer.Symbol;
import Lexer.Tokenizer;

public class Main {
    public static void main(String[] args) {

        InputBuffer ib = new InputBuffer("01010    01010.01.010 01001 01101.19", new ErrorManager());

        Tokenizer tokenizer = new Tokenizer(ib);

        var symbolList = tokenizer.extractAllSymbols();

        for (Symbol symbol : symbolList) {
            System.out.println(symbol);
        }

    }
}