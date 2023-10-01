package Parser;

abstract public class Nonterminal {
    public final String name;
    public Nonterminal(String name) {
        this.name = name;
    }
    public abstract void parse(Parser parser);

    public MarkClosed apply(Parser parser, MarkClosed closer) {
        MarkOpened opener = parser.openBefore(closer);
        parse(parser);
        return parser.close(opener, TreeKind.valid(this));
    }
    public MarkClosed apply(Parser parser) {
        MarkOpened opener = parser.open();
        parse(parser);
        return parser.close(opener, TreeKind.valid(this));
    }
}
