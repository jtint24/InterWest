package LLParser;

import Lexer.Symbol;

public class LLTerminalParseTreeNode extends LLParseTreeNode {
    Symbol wrappedSymbol;

    public LLTerminalParseTreeNode(Symbol wrappedSymbol) {
        this.wrappedSymbol = wrappedSymbol;
    }

    @Override
    public String toString() {
        return "Terminal("+wrappedSymbol+")";
    }
}
