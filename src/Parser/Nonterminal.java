package Parser;

import Lexer.SymbolString;

import java.util.ArrayList;

public class Nonterminal {

    ArrayList<Definition> definitions;


    public class Definition {
        ArrayList<ParseVariable> definitionString;
    }
}
