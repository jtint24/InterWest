package Parser;

import ErrorManager.Error;
import Lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class NonterminalParseTreeNode extends ParseTreeNode {
    TreeKind kind;
    ArrayList<ParseTreeNode> children = new ArrayList<>();
    public NonterminalParseTreeNode(TreeKind kind) {
        this.kind = kind;
    }

    public void addChild(ParseTreeNode child) {
        children.add(child);
    }

    @Override
    public String getHierarchyString(int tabLevel) {
        StringBuilder retString = new StringBuilder("\t".repeat(tabLevel)+"Nonterminal(" + kind + ", [\n");
        for (int i = 0; i < children.size(); i++) {
            ParseTreeNode child = children.get(i);
            retString.append(child.getHierarchyString(tabLevel+1));
            if ( i != children.size()-1) {
                retString.append(",");
            }
            retString.append("\n");
        }
        return retString+"\t".repeat(tabLevel)+"])";
    }

    @Override
    public void removeSymbolsOfType(Token t) {
        ArrayList<ParseTreeNode> newChildren = new ArrayList<>();

        for (ParseTreeNode child : children) {
            if (child instanceof TerminalParseTreeNode) {
                if (!((TerminalParseTreeNode) child).wrappedSymbol.getTokenType().equals(t)) {
                    newChildren.add(child);
                }
            } else if (child instanceof NonterminalParseTreeNode) {
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
        for (ParseTreeNode child : children) {
            errors.addAll(child.getMalformedNodeErrors());
        }
        return errors;
    }

    public String getSimplifiedHierarchyString(int tabLevel) {
        String header = "|\t".repeat(tabLevel)+"| "+kind + "\n";
        StringBuilder body = new StringBuilder();
        for (ParseTreeNode child : children) {
            body.append(child.getSimplifiedHierarchyString(tabLevel + 1));
        }
        if (tabLevel == 0) {
            body.deleteCharAt(body.length()-1);
        }
        return header + body;
    }



    @Override
    public String toString() {
        return getSimplifiedHierarchyString();
    }

    public String getFlatString() {
        StringBuilder retString = new StringBuilder("Nonterminal(" + kind + ", [");
        for (int i = 0; i < children.size(); i++) {
            ParseTreeNode child = children.get(i);
            retString.append(child);
            if ( i != children.size()-1) {
                retString.append(", ");
            }
        }
        return retString+"])";
    }
}
