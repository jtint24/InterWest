package Parser;

import ErrorManager.Error;

import Lexer.Symbol;
import Lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class TerminalParseTreeNode extends ParseTreeNode {
    Symbol wrappedSymbol;
    public TerminalParseTreeNode(Symbol wrappedSymbol) {
        this.wrappedSymbol = wrappedSymbol;
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
