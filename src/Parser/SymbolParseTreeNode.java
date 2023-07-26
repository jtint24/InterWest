package Parser;

import Lexer.Symbol;
import Lexer.SymbolString;

public class SymbolParseTreeNode extends ParseTreeNode {
    private final Symbol representativeSymbol;

    public SymbolParseTreeNode(Symbol symbol) {
        super();
        this.representativeSymbol = symbol;
    }

    @Override
    public SymbolString extractRepresentativeString() {
        return new SymbolString(representativeSymbol);
    }


    @Override
    protected void printTreeRepresentation(int i) {
        System.out.println("\t".repeat(i)+"- "+representativeSymbol);
    }

    @Override
    public void addChild(ParseTreeNode ptn) {

    }

    @Override
    public void printTreeRepresentation() {
        printTreeRepresentation(0);
    }

    public Symbol getSymbol() {
        return representativeSymbol;
    }

    @Override
    public Object clone() {
        return new SymbolParseTreeNode(representativeSymbol);
    }
}
