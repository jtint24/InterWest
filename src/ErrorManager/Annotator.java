package ErrorManager;

import Lexer.Symbol;
import Lexer.SymbolString;
import Parser.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Annotator {

    ParseTreeNode ptNode;

    HashMap<ParseTreeNode, Style> styles = new HashMap<>();

    public Annotator(ParseTreeNode ptNode) {
        this.ptNode = ptNode;
    }

    public void applyStyle(ParseTreeNode ptNode, Style style) {
        styles.put(ptNode, style);
    }

    ArrayList<Style> getStyleList(ParseTreeNode ptNode) {
        return getStyleList(ptNode, new Style("", null));
    }
    ArrayList<Style> getStyleList(ParseTreeNode ptNode, Style defaultStyle) {
        defaultStyle = styles.getOrDefault(ptNode, defaultStyle);
        if (ptNode instanceof TerminalParseTreeNode) {
            Style finalDefaultStyle = defaultStyle;
            return new ArrayList<>() {{
                add(finalDefaultStyle);
            }};
        } else {
            ArrayList<Style> styles = new ArrayList<>();


            for (ParseTreeNode child : ((NonterminalParseTreeNode) ptNode).getAllChildren()) {
                styles.addAll(getStyleList(child, defaultStyle));
            }

            return styles;
        }
    }

    int printedLength(String str) {
        return str.replaceAll("(\\x9B|\\x1B\\[)[0-?]*[ -\\/]*[@-~]", "").length();
    }

    public String getAnnotatedString() {
        // Get a sequence of applied Styles matching the SymbolString

        ArrayList<Style> styles = getStyleList(ptNode);
        SymbolString symbols = ptNode.getSymbols();

        System.out.println("Starting get annotated string");
        System.out.println(styles);
        System.out.println(symbols);

        // Render each Symbol with each Style to make an ArrayList of lines
        ArrayList<ArrayList<String>> lineBlocks = new ArrayList<>();
        int tallestBlockHeight = 0;

        for (int i = styles.size()-1; i>=0; i--) {
            Symbol symbol = symbols.get(i);
            Style style = styles.get(i);
            boolean isTerminal = (i == 0) || (styles.get(i-1).annotation == null);
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
                lines.set(i, lines.get(i) + " ".repeat(neededLength));
            }
        }

        // Turn the lines into a single string by joining them with newlines
        StringBuilder body = new StringBuilder();

        for (String line : lines) {
            body.append(line).append("\n");
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

            lines.add(ansiColor+s+AnsiCodes.RESET);
            if (underline != null) {
                int startingWhitespaceCount = s.length()-s.stripTrailing().length();
                int trailingWhitespaceCount = s.length()-s.stripLeading().length();
                int coreLength = s.strip().length();

                lines.add(" ".repeat(startingWhitespaceCount)+(""+underline).repeat(coreLength)+" ".repeat(trailingWhitespaceCount));
            } else if (annotation != null && isTerminal) {
                lines.add("|");
            }
            if (annotation != null && isTerminal) {
                while (lines.size() <= tallestBlockHeight) {
                    lines.add("|");
                }
                lines.add(annotation);
            }

            return lines;
        }
    }
}
