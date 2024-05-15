package Parser;

import ErrorManager.Error;

import Lexer.Symbol;
import Lexer.SymbolString;
import Lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class TerminalParseTreeNode extends ParseTreeNode {
    Symbol wrappedSymbol;
    SymbolString line;
    public TerminalParseTreeNode(Symbol wrappedSymbol, SymbolString symbols) {
        this.wrappedSymbol = wrappedSymbol;
        this.line = symbols.symbolsOnLine(wrappedSymbol.getStartingLineNumber());
    }

    public Symbol getWrappedSymbol() {
        return wrappedSymbol;
    }
    @Override
    public String getHierarchyString(int tabLevel) {
        return "\t".repeat(tabLevel) + wrappedSymbol;
    }

    @Override
    String getSimplifiedHierarchyString(int tabLevel) {
        return "|\t".repeat(tabLevel)+"| "+wrappedSymbol+"\n";
    }

    @Override
    public SymbolString getSymbols() {
        return new SymbolString(wrappedSymbol);
    }

    @Override
    public int getStartingLineNumber() {
        return wrappedSymbol.getStartingLineNumber();
    }

    @Override
    public SymbolString getLine() {
        return line;
    }

    @Override
    public void removeSymbolsOfType(Token t) {}
    @Override
    public List<Error> getMalformedNodeErrors() {
        return new ArrayList<>();
    }
    @Override
    public String toString() {
        return "Terminal("+wrappedSymbol+")";
    }
}
