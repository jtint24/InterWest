package LLParser;

import Lexer.Symbol;
import Lexer.Token;

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
    public String toString() {
        return "Terminal("+wrappedSymbol+")";
    }
}
