package LLParser;

import Lexer.Token;

public abstract class LLParseTreeNode {

    public String getHierarchyString() {
        return getHierarchyString(0);
    }

    abstract String getHierarchyString(int tabLevel);

    public abstract void removeSymbolsOfType(Token token);

}
