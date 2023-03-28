package Parser;

import Lexer.SymbolString;

import java.util.ArrayList;

public class NonterminalParseTreeNode extends ParseTreeNode {
    private final Nonterminal representativeNonterminal;
    private final Nonterminal.Definition definition;

    private final ArrayList<ParseTreeNode> childNodes;

    public NonterminalParseTreeNode(Nonterminal representativeNonterminal, Nonterminal.Definition definition) {
        this.representativeNonterminal = representativeNonterminal;
        this.definition = definition;
        this.childNodes = new ArrayList<>();
    }

    public void addChild(ParseTreeNode ptn) {
        childNodes.add(ptn);
    }

    public void insertAtStart(ParseTreeNode matchNode) {
        childNodes.add(0, matchNode);
    }

    @Override
    public SymbolString extractRepresentativeString() {
        SymbolString retString = new SymbolString();

        for (ParseTreeNode ptNode : childNodes) {
            retString.append(ptNode.extractRepresentativeString());
        }

        return retString;
    }

    /**
     * mergeWith
     *
     * For another parse tree, PT_B, this method adds all of PT_B's children as this node's own children,
     * */
    public void mergeWith(NonterminalParseTreeNode remainingParseTree) {
        childNodes.addAll(remainingParseTree.getChildNodes());
    }

    private ArrayList<ParseTreeNode> getChildNodes() {
        return childNodes;
    }

    @Override
    protected void printTreeRepresentation(int i) {
        System.out.println("\t".repeat(i)+"- "+representativeNonterminal+" "+definition);
        for (ParseTreeNode childNode : childNodes) {
            childNode.printTreeRepresentation(i+1);
        }
    }

    @Override
    public void printTreeRepresentation() {
        printTreeRepresentation(0);
    }
}
