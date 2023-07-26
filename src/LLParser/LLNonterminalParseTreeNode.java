package LLParser;

import ErrorManager.ErrorManager;
import ErrorManager.Error;
import Lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class LLNonterminalParseTreeNode extends LLParseTreeNode {
    TreeKind kind;
    ArrayList<LLParseTreeNode> children = new ArrayList<>();
    public LLNonterminalParseTreeNode(TreeKind kind) {
        this.kind = kind;
    }

    public void addChild(LLParseTreeNode child) {
        children.add(child);
    }

    @Override
    public String getHierarchyString(int tabLevel) {
        StringBuilder retString = new StringBuilder("\t".repeat(tabLevel)+"Nonterminal(" + kind + ", [\n");
        for (int i = 0; i < children.size(); i++) {
            LLParseTreeNode child = children.get(i);
            retString.append(child.getHierarchyString(tabLevel+1));
            if ( i != children.size()-1) {
                retString.append(", ");
            }
            retString.append("\n");
        }
        return retString+"\t".repeat(tabLevel)+"])";
    }

    @Override
    public void removeSymbolsOfType(Token t) {
        ArrayList<LLParseTreeNode> newChildren = new ArrayList<>();

        for (LLParseTreeNode child : children) {
            if (child instanceof LLTerminalParseTreeNode) {
                if (!((LLTerminalParseTreeNode) child).wrappedSymbol.getTokenType().equals(t)) {
                    newChildren.add(child);
                }
            } else if (child instanceof  LLNonterminalParseTreeNode) {
                child.removeSymbolsOfType(t);
                newChildren.add(child);
            }
        }

        children = newChildren;
    }

    @Override
    public List<Error> getMalformedNodeErrors() {
        ArrayList<Error> errors = new ArrayList<>();
        if (!kind.isValid) {
            errors.add(new Error(Error.ErrorType.PARSER_ERROR, "Malformed tree node", true));
        }
        for (LLParseTreeNode child : children) {
            errors.addAll(child.getMalformedNodeErrors());
        }
        return errors;
    }



    @Override
    public String toString() {
        StringBuilder retString = new StringBuilder("Nonterminal(" + kind + ", [");
        for (int i = 0; i < children.size(); i++) {
            LLParseTreeNode child = children.get(i);
            retString.append(child);
            if ( i != children.size()-1) {
                retString.append(", ");
            }
        }
        return retString+"])";
    }
}
