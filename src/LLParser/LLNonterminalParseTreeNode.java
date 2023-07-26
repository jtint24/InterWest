package LLParser;

import java.util.ArrayList;

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
