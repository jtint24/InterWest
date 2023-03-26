package Lexer;

import java.util.ArrayList;

public class SymbolString {
    private ArrayList<Symbol> symbols;

    public SymbolString(ArrayList<Symbol> symbols) {
        this.symbols = symbols;
    }

    public SymbolString() {
        this.symbols = new ArrayList<>();
    }

    public SymbolString(Symbol... initSymbols) {
        this();
        for (Symbol symbol : initSymbols) {
            append(symbol);
        }
    }

    public void append(Symbol s) {
        symbols.add(s);
    }
    public void append(SymbolString string2) {
        symbols.addAll(string2.symbols);
    }
}
