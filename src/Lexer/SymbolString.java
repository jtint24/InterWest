package Lexer;

import java.util.ArrayList;
import java.util.List;

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

    public SymbolString symbolsOnLine(int lineNumber) {
        ArrayList<Symbol> retString = new ArrayList<>();
        for (Symbol s : symbols) {
            if (s.getStartingLineNumber() == lineNumber) {
                retString.add(s);
            }
        }
        return new SymbolString(retString);
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

    public List<Symbol> toList() {
        return symbols;
    }

}
