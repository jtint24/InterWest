package Parser;

import ErrorManager.Error;

import Lexer.SymbolString;
import Lexer.Token;


import java.util.List;

public abstract class ParseTreeNode {
    public String getHierarchyString() {
        return getHierarchyString(0);
    }
    abstract String getHierarchyString(int tabLevel);
    abstract String getSimplifiedHierarchyString(int tabLevel);
    public String getSimplifiedHierarchyString() { return getSimplifiedHierarchyString(0); }
    public abstract void removeSymbolsOfType(Token token);
    public abstract List<Error> getMalformedNodeErrors();
    public abstract SymbolString getSymbols();
}
