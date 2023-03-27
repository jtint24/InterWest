package Parser;

import java.util.ArrayList;

public class NonterminalParseTreeNode extends ParseTreeNode {
    private Nonterminal representativeNonterminal;
    private Nonterminal.Definition definition;

    private ArrayList<ParseTreeNode> childNodes;
}
