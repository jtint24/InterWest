package Parser;

public class TreeKind {
    Nonterminal validTreeType;
    boolean isValid;

    public TreeKind(Nonterminal validTreeType, boolean isValid) {
        this.validTreeType = validTreeType;
        this.isValid = isValid;
    }

    public static TreeKind invalid() {
        return new TreeKind(null, false);
    }
    public static TreeKind valid(Nonterminal validTreeType) {
        return new TreeKind(validTreeType, true);
    }

    public String getName() {
        if (isValid) {
            return validTreeType.name;
        } else {
            return "INVALID";
        }
    }
    @Override
    public String toString() {
        if (isValid) {
            return "TreeKind("+validTreeType.name+")";
        } else {
            return "TreeKind(INVALID)";
        }
    }
}
