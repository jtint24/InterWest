package Parser;

import Lexer.SymbolString;

public abstract class ParseTreeNode implements Cloneable {
    /**
     * extractRepresentativeString
     *
     * Get the string that is represented by the ParseTree with this node as the root.
     * */
    public abstract SymbolString extractRepresentativeString();

    public abstract void printTreeRepresentation();
    protected abstract void printTreeRepresentation(int i);

    public abstract void addChild(ParseTreeNode ptn);

    public abstract Object clone();
}
