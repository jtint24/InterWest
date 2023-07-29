package LLParser;

import ErrorManager.ErrorManager;
import ErrorManager.Error;

import Lexer.Token;


import java.util.ArrayList;
import java.util.List;

public abstract class LLParseTreeNode {
    public String getHierarchyString() {
        return getHierarchyString(0);
    }
    abstract String getHierarchyString(int tabLevel);
    public abstract void removeSymbolsOfType(Token token);
    public abstract List<Error> getMalformedNodeErrors();

}
