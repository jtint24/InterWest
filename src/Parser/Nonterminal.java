package Parser;

import ErrorManager.ErrorManager;
import Lexer.SymbolString;
import Lexer.Token;
import Lexer.TokenLibrary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Nonterminal {
    private final ArrayList<Definition> definitions;
    private final String name;

    private final HashSet<Token> firstSet;
    private final HashSet<Token> followSet;



    public Nonterminal(String name, Definition... definitions) {
        this.name = name;
        this.definitions = new ArrayList<>();
        this.definitions.addAll(List.of(definitions));
        this.firstSet = new HashSet<>();
        this.followSet = new HashSet<>();
    }
    public ParseTreeNode buildParseTree(SymbolString inString,  ErrorManager errorManager) {
        for (Definition definition : definitions) {
            ParseTreeNode builtParseTree = definition.buildParseTree(this, inString, errorManager);
            if (builtParseTree != null) {
                return builtParseTree;
            }
        }

        return null;
    }

    public ParseVariable asParseVariable() {
        return new ParseVariable(this);
    }

    @Override
    public String toString() {
        return name;
    }

    public void addDefinitions(Definition... definitions) {
        this.definitions.addAll(List.of(definitions));
    }

    public boolean coallesceFirstSetMinusEp(HashSet<Token> otherFirstSet) {
        int oldSize = firstSet.size();

        boolean containsEp = firstSet.contains(TokenLibrary.getEpsilon());
        firstSet.addAll(otherFirstSet);
        if (!containsEp) {
            firstSet.remove(TokenLibrary.getEpsilon());
        }

        return oldSize != firstSet.size();
    }

    public HashSet<Token> getFirstSet() {
        return firstSet;
    }

    public boolean addToFirstSet(Token newToken) {
        boolean alreadyContained = firstSet.contains(newToken);
        firstSet.add(newToken);
        return !alreadyContained;
    }

    public ArrayList<Definition> getDefinitions() {
        return definitions;
    }

    public static class Definition {
        private final ArrayList<ParseVariable> definitionString;
        private final Nonterminal definedNT;

        public Definition(Nonterminal definedNT, ParseVariable... definitionString) {
            this.definedNT = definedNT;
            this.definitionString = new ArrayList<>();
            this.definitionString.addAll(List.of(definitionString));
        }

        public Definition(Definition definition, int i) {
            this.definedNT = definition.definedNT;
            this.definitionString = new ArrayList<>(definition.definitionString.subList(i, definition.definitionString.size()));
        }

        public ArrayList<ParseVariable> getDefinitionString() {
            return definitionString;
        }

        public Nonterminal getDefinedNT() {
            return definedNT;
        }

        public NonterminalParseTreeNode buildParseTree(Nonterminal nt, SymbolString inString, ErrorManager errorManager) {


            if (inString.length() == 0 && definitionString.size() == 0) {
                return new NonterminalParseTreeNode(nt, this);
            } else if (inString.length() == 0 && definitionString.size() != 0) {
                return null;
            } else if (definitionString.size() == 0 && inString.length()!= 0) {
                return null;
            }

            // Iterate over the s = [0...n] substring for n = 0...len(inString)
            // Check if the s substring matches the leading ParseVariable in the definition string
            // If it does, build a new definition that has the [1...] substring of the definition string and return that

            NonterminalParseTreeNode rootNode = new NonterminalParseTreeNode(nt, this);

            for (int i = 0; i<inString.length()+1; i++) {
                SymbolString leadingString = inString.substring(0,i);
                ParseVariable variableToMatch = definitionString.get(0);

                System.out.println("trying "+leadingString +" against "+variableToMatch);

                ParseTreeNode matchNode = variableToMatch.matches(leadingString, errorManager);
                if (matchNode != null) {
                    System.out.println("success! Leading parsed as " +matchNode.extractRepresentativeString());
                    Definition remainingDefinition = new Definition(this, 1);
                    NonterminalParseTreeNode remainingParseTree = remainingDefinition.buildParseTree(nt, inString.substring(i), errorManager);

                    System.out.println("remaining definition parsed as "+ (remainingParseTree==null ? "null" : remainingParseTree.extractRepresentativeString()));


                    if (remainingParseTree != null) {
                        rootNode.insertAtStart(matchNode);
                        rootNode.mergeWith(remainingParseTree);
                        return rootNode;
                    }
                }
            }

            return null;
        }

        @Override
        public String toString() {
            StringBuilder retString = new StringBuilder();
            for (ParseVariable defVariable : definitionString) {
                retString.append(defVariable);
                retString.append(" ");
            }
            return retString.toString();
        }

        public boolean hasLeadingEps(int i) {
            for (int n = 0; n<i; n++) {
                if (!definitionString.get(n).getFirstSet().contains(TokenLibrary.getEpsilon())) {
                    return false;
                }
            }
            return true;
        }
    }
}
