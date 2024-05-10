package ErrorManager;

import Lexer.Symbol;
import Lexer.SymbolString;
import Parser.*;

import java.util.ArrayList;
import java.util.HashMap;

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

            for (ParseTreeNode child : ((NonterminalParseTreeNode) ptNode).getChildren()) {
                styles.addAll(getStyleList(child, defaultStyle));
            }

            return styles;
        }
    }

    public String getAnnotatedString() {
        // Get a sequence of applied Styles matching the SymbolString

        ArrayList<Style> styles = getStyleList(ptNode);
        SymbolString symbols = ptNode.getSymbols();

        // Render each Symbol with each Style to make an ArrayList of lines
        ArrayList<ArrayList<String>> lineBlocks = new ArrayList<>();
        int tallestBlockHeight = 0;

        for (int i = styles.size()-1; i>=0; i--) {
            Symbol symbol = symbols.get(i);
            Style style = styles.get(i);
            ArrayList<String> renderedBlock = style.renderOn(symbol.getLexeme(), tallestBlockHeight);
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
                if (lines.get(i).length() < block.get(0).length()) {
                    neededLength += block.get(0).length() - block.get(i).length();
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

        public Style(String ansiColor, Character underline) {
            this.ansiColor = ansiColor;
            this.underline = underline;
        }

        public ArrayList<String> renderOn(String s, int tallestBlockHeight) {
            return new ArrayList<>() {{
                add(ansiColor+s+AnsiCodes.RESET);
                if (underline != null) {
                    add((""+underline).repeat(s.length()));
                }
            }};
        }
    }
}
