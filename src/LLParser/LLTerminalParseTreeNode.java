package LLParser;

import ErrorManager.ErrorManager;
import ErrorManager.Error;

import Lexer.Symbol;
import Lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class LLTerminalParseTreeNode extends LLParseTreeNode {
    Symbol wrappedSymbol;

    public LLTerminalParseTreeNode(Symbol wrappedSymbol) {
        this.wrappedSymbol = wrappedSymbol;
    }


    @Override
    public String getHierarchyString(int tabLevel) {
        return "\t".repeat(tabLevel) + wrappedSymbol;
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
