package ErrorManager;

import Lexer.Symbol;
import Lexer.SymbolString;
import Parser.*;

import java.util.*;

public class Annotator {

    SymbolString stringToAnnotate;

    ArrayList<Style> styles;

    private static final Style defaultStyle = new Style("", null);

    public Annotator(SymbolString symbolString) {
        this.stringToAnnotate = symbolString;
        this.styles = new ArrayList<>();
        for (int i = 0; i<symbolString.length(); i++) {
            styles.add(defaultStyle);
        }
    }

    public void applyStyle(ParseTreeNode ptNode, Style style) {
        HashSet<Symbol> styledSymbols = new HashSet<>(ptNode.getSymbols().toList());

        for (int i = 0; i<styles.size(); i++) {
            if (styledSymbols.contains(stringToAnnotate.get(i))) {
                styles.set(i, style);
            }
        }
    }

    int printedLength(String str) {
        return str.replaceAll("(\\x9B|\\x1B\\[)[0-?]*[ -\\/]*[@-~]", "").length();
    }

    public String getAnnotatedString() {
        // Get a sequence of applied Styles matching the SymbolString


        // System.out.println("Starting get annotated string");
        // System.out.println(styles);
        // System.out.println(symbols);

        // Render each Symbol with each Style to make an ArrayList of lines
        ArrayList<ArrayList<String>> lineBlocks = new ArrayList<>();
        int tallestBlockHeight = 0;

        boolean lastWasAnnotated = false;

        for (int i = styles.size()-1; i>=0; i--) {
            Symbol symbol = stringToAnnotate.get(i);
            Style style = styles.get(i);
            // isTerminal = isFirst or (isFirstNonBlankSymbolWithNonNullAnnotation)
            // isTerminal = isFirst or (isNonBlank && nonNullAnnotation && !lastWasAnnotated)
            boolean isTerminal = (i == 0 && !symbol.getLexeme().isBlank()) || (styles.get(i).annotation != null && !symbol.getLexeme().isBlank() && !lastWasAnnotated); // TODO: FIX THIS SO THAT ANNOTATED BLOCKS STARTING WITH WHITESPACE GET ANNOTATED PROPERLY (THE FIRST ANNOTATED TERMINAL SHOULD BE THE FIRST NONBLANK TERMINAL, NOT THE FIRST TERMINAL IN GENERAL)
            if (isTerminal) {
                lastWasAnnotated = true;
            } else if (styles.get(i).annotation == null) {
                lastWasAnnotated = false;
            }
            ArrayList<String> renderedBlock = style.renderOn(symbol.getLexeme(), tallestBlockHeight, isTerminal);
            lineBlocks.add(0, renderedBlock);
            tallestBlockHeight = Math.max(tallestBlockHeight, renderedBlock.size());
        }

        // Join each set of lines into one continuous group of lines
        ArrayList<String> lines = new ArrayList<>();

        for (int i = 0; i<tallestBlockHeight; i++) {
            lines.add("");
        }

        for (ArrayList<String> block : lineBlocks) {
            for (int i = 0; i<block.size(); i++) {
                lines.set(i, lines.get(i) + block.get(i));
            }
            for (int i = block.size(); i<tallestBlockHeight; i++) {
                int neededLength = 0;
                if (printedLength(lines.get(i)) < printedLength(lines.get(0))) {
                    neededLength = printedLength(lines.get(0)) - printedLength(lines.get(i));
                }
                // String filler = (i+"").charAt(0)+"";
                lines.set(i, lines.get(i) + " ".repeat(neededLength));
            }
        }

        // Create a block for line number heading


        // Turn the lines into a single string by joining them with newlines
        StringBuilder body = new StringBuilder();

        String lineNumberPrefix = stringToAnnotate.get(0).getStartingLineNumber()+" ";

        for (int i = 0; i<lines.size(); i++) {
            String prefix = i == 0 ? lineNumberPrefix : " ".repeat(lineNumberPrefix.length());
            String line = prefix + "| "+ lines.get(i);
            body.append(line.stripTrailing()).append("\n");
        }

        return body.toString();
    }

    public static class Style {
        String ansiColor;
        Character underline;
        String annotation;

        public Style(String ansiColor, Character underline) {
            this.ansiColor = ansiColor;
            this.underline = underline;
        }

        public Style(String ansiColor, Character underline, String annotation) {
            this.ansiColor = ansiColor;
            this.underline = underline;
            this.annotation = annotation;
        }

        public ArrayList<String> renderOn(String s, int tallestBlockHeight, boolean isTerminal) {


            ArrayList<String> lines = new ArrayList<>();
            String flattenedCore = s;
            if (s.contains("\n")) {
                flattenedCore = s.substring(0, s.indexOf('\n'));
            }
            lines.add(ansiColor+flattenedCore+AnsiCodes.RESET);

            if (underline != null) {
                if (s.isBlank()) {
                    lines.add(s);

                } else {
                    int startingWhitespaceCount = s.length() - s.stripTrailing().length();
                    int trailingWhitespaceCount = s.length() - s.stripLeading().length();
                    int coreLength = s.strip().length();
                    if (s.contains("0") && underline == '^') {
                        String str = " ".repeat(startingWhitespaceCount) + ("" + underline).repeat(coreLength) + " ".repeat(trailingWhitespaceCount);
                    }
                    assert startingWhitespaceCount + trailingWhitespaceCount + coreLength == s.length();
                    lines.add("-".repeat(startingWhitespaceCount) + ("" + underline).repeat(coreLength) + ".".repeat(trailingWhitespaceCount));
                    //lines.add((""+underline).repeat(s.length()));
                }
            } else if (annotation != null && isTerminal) {
                lines.add("|");
            }
            if (annotation != null && !s.isBlank() && isTerminal) {
                do {
                    lines.add("|");
                } while (lines.size() <= tallestBlockHeight);
                lines.add(annotation);
            }

            // System.out.println(Arrays.toString(lines.toArray()));

            return lines;
        }
    }
}
