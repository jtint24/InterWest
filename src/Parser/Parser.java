package Parser;


import ErrorManager.ErrorManager;
import Lexer.*;

import java.util.*;

import Parser.EventLibrary.*;
import ErrorManager.Error;

import static ErrorManager.ErrorLibrary.getParserHasTerminated;
import static ErrorManager.ErrorLibrary.getWrongToken;

public class Parser {
    SymbolString symbols;
    int pos = 0;
    int fuel = 256;
    ArrayList<Event> events = new ArrayList<>();
    ErrorManager errorManager;
    Tokenizer tokenizer;

     public Parser(Tokenizer tokenizer, ErrorManager errorManager) {
         this.errorManager = errorManager;
         this.tokenizer = tokenizer;
     }

     public void setSymbols(SymbolString symbols) {
         this.symbols = symbols;
     }
     MarkOpened open() {
         MarkOpened mark = new MarkOpened(this.events.size());
         this.events.add(new OpenEvent(TreeKind.invalid()));
         return mark;
     }

     MarkOpened openBefore(MarkClosed closer) {
        MarkOpened opener = new MarkOpened(closer.index);
        events.add(closer.index, new OpenEvent(TreeKind.invalid()));
        return opener;
     }

      MarkClosed close(MarkOpened m, TreeKind kind) {
         this.events.set(m.index, new OpenEvent(kind));
         CloseEvent closer = new CloseEvent();
         this.events.add(closer);
         return new MarkClosed(m.index);
     }

     void advance() {
         assert !this.eof();
         this.fuel = 256;
         this.events.add(new AdvanceEvent());
         this.pos++;
     }
     boolean eof() {
         return this.pos == this.symbols.length();
     }

     Token nth(int lookahead) {
         if (this.fuel == 0) {
             // TODO: Figure out how to report this as an error
             throw new RuntimeException();
             // errorManager.logError(new Error(Error.ErrorType.PARSER_ERROR, "Parser is stuck!", true));
         }

         this.fuel--;

         if (this.pos+lookahead >= this.symbols.length()) {
             errorManager.logError(getParserHasTerminated());
         }

         Token retToken = this.symbols.get(this.pos+lookahead).getTokenType();
         return retToken;
     }

     boolean at(Token kind) {
         if (this.eof()) {
             return false;
         }
         return this.nth(0) == kind;
     }

     boolean eat(Token kind) {
         if (!this.eof() && (this.at(TokenLibrary.inlineComment) || this.at(TokenLibrary.blockComment))) {
             this.advance();
         }
         if (!this.eof() && this.at(kind)) {
             this.advance();
             return true;
         } else {
             return false;
         }
     }

     void expect(Token kind) {
         if (this.eat(kind)) {
             return;
         }
         errorManager.logError(getWrongToken(kind, this.nth(0)));
         // errorManager.logError(new Error(Error.ErrorType.PARSER_ERROR, "Expected token of type "+kind+", got "+this.nth(0), true));
     }

     void advanceWithError(Error error) {
         MarkOpened markOpened = this.open();
         errorManager.logError(error);
         this.advance();
         this.close(markOpened, TreeKind.invalid());
     }

     public ParseTreeNode buildTree() {
         Stack<ParseTreeNode> stack = new Stack<>();

         Iterator<Symbol> symbolIterator = symbols.toList().iterator();

         assert events.get(events.size()-1) instanceof CloseEvent;

         for (Event event : events) {
             if (event instanceof OpenEvent) {
                 stack.push(new NonterminalParseTreeNode(((OpenEvent) event).kind, symbols));
             } else if (event instanceof CloseEvent) {
                 ParseTreeNode tree = stack.pop();
                 if (stack.isEmpty()) {
                     stack.push(tree);
                 } else {
                     ((NonterminalParseTreeNode) stack.peek()).addChild(tree);
                 }
             } else if (event instanceof AdvanceEvent) {
                 Symbol symbol = symbolIterator.next();
                 ((NonterminalParseTreeNode) stack.peek()).addChild(new TerminalParseTreeNode(symbol, symbols));
             }
         }
         assert stack.size() == 1;

         return stack.pop();
     }
}
