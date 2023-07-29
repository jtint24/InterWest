import Interpreter.InterpretationSession;
import Testing.Test;
import Testing.Test.TestFunction;
import Testing.TestSuite;
import Testing.Testable;


public class Main {
    public static void main(String[] args) {

        // InterpretationSession newSession = new InterpretationSession("let a = 5 \nlet b = a \nlet c = a(1,2,\"hello\")\n let d = wep(a) let e = a(b(c(d(e))))");

        // newSession.runSession();

        TestSuite testSuite = new TestSuite(
                "Testing",

                new TestSuite(
                        "Lexer",
                        new Test(
                            "let a = 5 \nlet b = a \nlet c = a(1,2,\"hello\")\n let d = wep(a) let e = a(b(c(d(e))))",
                            "{ lexeme = `let` tokenType = let }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `a` tokenType = identifier }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `=` tokenType = = }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `5` tokenType = int }{ lexeme = ` \\n` tokenType = whitespace }{ lexeme = `let` tokenType = let }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `b` tokenType = identifier }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `=` tokenType = = }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `a` tokenType = identifier }{ lexeme = ` \\n` tokenType = whitespace }{ lexeme = `let` tokenType = let }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `c` tokenType = identifier }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `=` tokenType = = }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `a` tokenType = identifier }{ lexeme = `(` tokenType = ( }{ lexeme = `1` tokenType = int }{ lexeme = `,` tokenType = , }{ lexeme = `2` tokenType = int }{ lexeme = `,` tokenType = , }{ lexeme = `\"hello\"` tokenType = String }{ lexeme = `)` tokenType = ) }{ lexeme = `\\n ` tokenType = whitespace }{ lexeme = `let` tokenType = let }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `d` tokenType = identifier }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `=` tokenType = = }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `wep` tokenType = identifier }{ lexeme = `(` tokenType = ( }{ lexeme = `a` tokenType = identifier }{ lexeme = `)` tokenType = ) }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `let` tokenType = let }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `e` tokenType = identifier }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `=` tokenType = = }{ lexeme = ` ` tokenType = whitespace }{ lexeme = `a` tokenType = identifier }{ lexeme = `(` tokenType = ( }{ lexeme = `b` tokenType = identifier }{ lexeme = `(` tokenType = ( }{ lexeme = `c` tokenType = identifier }{ lexeme = `(` tokenType = ( }{ lexeme = `d` tokenType = identifier }{ lexeme = `(` tokenType = ( }{ lexeme = `e` tokenType = identifier }{ lexeme = `)` tokenType = ) }{ lexeme = `)` tokenType = ) }{ lexeme = `)` tokenType = ) }{ lexeme = `)` tokenType = ) }\n",
                            TestFunction.lexer
                        ),
                        new Test(
                            "let",
                            "{ lexeme = `let` tokenType = let }\n",
                            TestFunction.lexer
                        )
                ),
                new TestSuite(
                        "Parser",
                        new Test(
                                "let a = 5",
                                "Nonterminal(TreeKind(file), [Nonterminal(TreeKind(Expression), [Nonterminal(TreeKind(let), [Terminal({ lexeme = `let` tokenType = let }), Terminal({ lexeme = ` ` tokenType = whitespace }), Terminal({ lexeme = `a` tokenType = identifier }), Terminal({ lexeme = ` ` tokenType = whitespace }), Terminal({ lexeme = `=` tokenType = = }), Terminal({ lexeme = ` ` tokenType = whitespace }), Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `5` tokenType = int })])])])])\n",
                                TestFunction.parser
                        ),
                        new Test(
                                "let a = 5 \nlet b = a \nlet c = a(1,2,\"hello\")\n let d = wep(a) let e = a(b(c(d(e))))",
                                "Nonterminal(TreeKind(file), [Nonterminal(TreeKind(Expression), [Nonterminal(TreeKind(let), [Terminal({ lexeme = `let` tokenType = let }), Terminal({ lexeme = ` ` tokenType = whitespace }), Terminal({ lexeme = `a` tokenType = identifier }), Terminal({ lexeme = ` ` tokenType = whitespace }), Terminal({ lexeme = `=` tokenType = = }), Terminal({ lexeme = ` ` tokenType = whitespace }), Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `5` tokenType = int })])])]), Terminal({ lexeme = ` \\n` tokenType = whitespace }), Nonterminal(TreeKind(Expression), [Nonterminal(TreeKind(let), [Terminal({ lexeme = `let` tokenType = let }), Terminal({ lexeme = ` ` tokenType = whitespace }), Terminal({ lexeme = `b` tokenType = identifier }), Terminal({ lexeme = ` ` tokenType = whitespace }), Terminal({ lexeme = `=` tokenType = = }), Terminal({ lexeme = ` ` tokenType = whitespace }), Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `a` tokenType = identifier })])])]), Terminal({ lexeme = ` \\n` tokenType = whitespace }), Nonterminal(TreeKind(Expression), [Nonterminal(TreeKind(let), [Terminal({ lexeme = `let` tokenType = let }), Terminal({ lexeme = ` ` tokenType = whitespace }), Terminal({ lexeme = `c` tokenType = identifier }), Terminal({ lexeme = ` ` tokenType = whitespace }), Terminal({ lexeme = `=` tokenType = = }), Terminal({ lexeme = ` ` tokenType = whitespace }), Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `a` tokenType = identifier }), Terminal({ lexeme = `(` tokenType = ( }), Nonterminal(TreeKind(Argument List), [Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `1` tokenType = int })]), Terminal({ lexeme = `,` tokenType = , }), Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `2` tokenType = int })]), Terminal({ lexeme = `,` tokenType = , }), Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `\"hello\"` tokenType = String })])]), Terminal({ lexeme = `)` tokenType = ) })])])]), Terminal({ lexeme = `\\n ` tokenType = whitespace }), Nonterminal(TreeKind(Expression), [Nonterminal(TreeKind(let), [Terminal({ lexeme = `let` tokenType = let }), Terminal({ lexeme = ` ` tokenType = whitespace }), Terminal({ lexeme = `d` tokenType = identifier }), Terminal({ lexeme = ` ` tokenType = whitespace }), Terminal({ lexeme = `=` tokenType = = }), Terminal({ lexeme = ` ` tokenType = whitespace }), Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `wep` tokenType = identifier }), Terminal({ lexeme = `(` tokenType = ( }), Nonterminal(TreeKind(Argument List), [Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `a` tokenType = identifier })])]), Terminal({ lexeme = `)` tokenType = ) })])])]), Terminal({ lexeme = ` ` tokenType = whitespace }), Nonterminal(TreeKind(Expression), [Nonterminal(TreeKind(let), [Terminal({ lexeme = `let` tokenType = let }), Terminal({ lexeme = ` ` tokenType = whitespace }), Terminal({ lexeme = `e` tokenType = identifier }), Terminal({ lexeme = ` ` tokenType = whitespace }), Terminal({ lexeme = `=` tokenType = = }), Terminal({ lexeme = ` ` tokenType = whitespace }), Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `a` tokenType = identifier }), Terminal({ lexeme = `(` tokenType = ( }), Nonterminal(TreeKind(Argument List), [Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `b` tokenType = identifier }), Terminal({ lexeme = `(` tokenType = ( }), Nonterminal(TreeKind(Argument List), [Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `c` tokenType = identifier }), Terminal({ lexeme = `(` tokenType = ( }), Nonterminal(TreeKind(Argument List), [Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `d` tokenType = identifier }), Terminal({ lexeme = `(` tokenType = ( }), Nonterminal(TreeKind(Argument List), [Nonterminal(TreeKind(Expression), [Terminal({ lexeme = `e` tokenType = identifier })])]), Terminal({ lexeme = `)` tokenType = ) })])]), Terminal({ lexeme = `)` tokenType = ) })])]), Terminal({ lexeme = `)` tokenType = ) })])]), Terminal({ lexeme = `)` tokenType = ) })])])])])\n",
                                TestFunction.parser
                        )
                )
        );

        testSuite.getResults();


    }
}