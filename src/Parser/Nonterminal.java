package Parser;

import ErrorManager.ErrorManager;
import Lexer.SymbolString;

import java.util.ArrayList;
import java.util.List;

public class Nonterminal {
    private final ArrayList<Definition> definitions;


    public Nonterminal(Definition... definitions) {
        this.definitions = new ArrayList<>();
        this.definitions.addAll(List.of(definitions));
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

    public static class Definition {
        private final ArrayList<ParseVariable> definitionString;

        public Definition(ParseVariable... definitionString) {
            this.definitionString = new ArrayList<>();
            this.definitionString.addAll(List.of(definitionString));
        }

        public Definition(Definition definition, int i) {
            this.definitionString = (ArrayList<ParseVariable>) definition.definitionString.subList(i, definition.definitionString.size());
        }

        public NonterminalParseTreeNode buildParseTree(Nonterminal nt, SymbolString inString, ErrorManager errorManager) {


            // Iterate over the s = [0...n] substring for n = 0...len(inString)
            // Check if the s substring matches the leading ParseVariable in the definition string
            // If it does, build a new definition that has the [1...] substring of the definition string and return that

            NonterminalParseTreeNode rootNode = new NonterminalParseTreeNode(nt, this);

            for (int i = 0; i<inString.length(); i++) {
                SymbolString leadingString = inString.substring(0,i);
                ParseVariable variableToMatch = definitionString.get(0);

                ParseTreeNode matchNode = variableToMatch.matches(leadingString, errorManager);
                if (matchNode != null) {
                    Definition remainingDefinition = new Definition(this, 1);
                    NonterminalParseTreeNode remainingParseTree = remainingDefinition.buildParseTree(nt, inString.substring(i+1), errorManager);
                    if (remainingParseTree != null) {
                        rootNode.insertAtStart(matchNode);
                        rootNode.mergeWith(remainingParseTree);
                        return rootNode;
                    }
                }
            }

            return null;
        }


    }
}
