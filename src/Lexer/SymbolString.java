package Lexer;

import java.util.ArrayList;

public class SymbolString {
    private final ArrayList<Symbol> symbols;

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
    public Symbol get(int i) { return symbols.get(i); }

    public int length() {
        return symbols.size();
    }

    public SymbolString substring(int startIdx, int untilIdx) {
        return new SymbolString(new ArrayList<>(symbols.subList(startIdx, untilIdx)));
    }

    public SymbolString substring(int untilIdx) {
        return substring(untilIdx, symbols.size());
    }

    public String toString() {
        StringBuilder retString = new StringBuilder();
        for (Symbol s : symbols) {
            retString.append(s);
        }
        return retString.toString();
    }

    public ArrayList<Symbol> toList() {
        return symbols;
    }
}
