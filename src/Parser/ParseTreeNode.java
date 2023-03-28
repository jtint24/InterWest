package Parser;

import Lexer.SymbolString;

public abstract class ParseTreeNode {
    /**
     * extractRepresentativeString
     *
     * Get the string that is represented by the ParseTree with this node as the root.
     * */
    public abstract SymbolString extractRepresentativeString();
}
