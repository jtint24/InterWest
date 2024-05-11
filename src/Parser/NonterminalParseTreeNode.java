package Parser;

import ErrorManager.Error;
import Lexer.SymbolString;
import Lexer.Token;
import Lexer.TokenLibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ErrorManager.ErrorLibrary.getSyntaxError;

public class NonterminalParseTreeNode extends ParseTreeNode {
    TreeKind kind;
    private final ArrayList<ParseTreeNode> children = new ArrayList<>();
    public NonterminalParseTreeNode(TreeKind kind) {
        this.kind = kind;
    }

    public void addChild(ParseTreeNode child) {
        children.add(child);
    }

    public TreeKind getKind() {
        return kind;
    }

    public List<ParseTreeNode> getChildren() {
        return children.stream().filter(
                (child) -> (child instanceof NonterminalParseTreeNode) || !((TerminalParseTreeNode) child).wrappedSymbol.getTokenType().equals(TokenLibrary.whitespace)
        ).collect(Collectors.toList());
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
        // TODO: DELETE (or remove references)
        /*
        ArrayList<ParseTreeNode> newChildren = new ArrayList<>();

        for (ParseTreeNode child : children) {
            if (child instanceof TerminalParseTreeNode) {
                if (!((TerminalParseTreeNode) child).wrappedSymbol.getTokenType().equals(t)) {
                    newChildren.add(child);
                }
            } else if (child instanceof NonterminalParseTreeNode) {
                // child.removeSymbolsOfType(t);
                newChildren.add(child);
            }
        }

        children = newChildren;

         */
    }

    public SymbolString getSymbols() {
        SymbolString str = new SymbolString();
        for (ParseTreeNode child : children) {
            str.append(child.getSymbols());
        }
        return str;
    }

    @Override
    public List<Error> getMalformedNodeErrors() {
        ArrayList<Error> errors = new ArrayList<>();
        if (!kind.isValid) {
            errors.add(getSyntaxError(this));
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

    /**
     * Gets all children including whitespace (Generally this is unwanted)
     * */
    public List<ParseTreeNode> getAllChildren() {
        return children;
    }
}
