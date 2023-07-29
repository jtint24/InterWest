package Parser;

abstract public class Nonterminal {
    String name;
    public Nonterminal(String name) {
        this.name = name;
    }
    public abstract void apply(Parser parser);
}
