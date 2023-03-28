package Parser;

import ErrorManager.ErrorManager;
import Lexer.Symbol;
import Lexer.SymbolString;

import java.util.ArrayList;

public class Nonterminal {
    ArrayList<Definition> definitions;

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
        ArrayList<ParseVariable> definitionString;

        public Definition(Definition definition, int i) {
            this.definitionString = (ArrayList<ParseVariable>) definition.definitionString.subList(i, definition.definitionString.size());
        }

        public NonterminalParseTreeNode buildParseTree(Nonterminal nt, SymbolString inString, ErrorManager errorManager) {


            // Iterate over the s = [0...n] substring for n = 0...len(inString)
            // Check if the s substring matches the leading ParseVariable in the definition string
            // If it does, build a new definition that has the [1...] substring of the definition string and return that


            for (int i = 0; i<inString.length(); i++) {
                SymbolString leadingString = inString.substring(0,i);
                ParseVariable variableToMatch = definitionString.get(0);

                ParseTreeNode matchNode = variableToMatch.matches(leadingString, errorManager);
                if (matchNode != null) {
                    Definition remainingDefinition = new Definition(this, 1);
                    NonterminalParseTreeNode remainingParseTree = remainingDefinition.buildParseTree(nt, inString.substring(i+1), errorManager);
                    if (remainingParseTree != null) {
                        remainingParseTree.insertAtStart(matchNode);
                        return remainingParseTree;
                    }
                }
            }

            return null;
        }


    }
}
