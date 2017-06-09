package com.github.kaaz.emily.util;

import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Made by nija123098 on 3/18/2017.
 */
public class FormatHelper {
    public static String repeat(char c, int i) {
        char[] chars = new char[i];
        Arrays.fill(chars, c);
        return new String(chars);
    }
    public static String reduceRepeats(String s, char c){// use index of to optimize
        final StringBuilder builder = new StringBuilder();
        boolean repeat = false;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c){
                if (!repeat){
                    builder.append(c);
                }
                repeat = true;
            }else{
                repeat = false;
                builder.append(s.charAt(i));
            }
        }
        return builder.toString();
    }
    public static String removeChars(String s, char toRemove){
        StringBuilder builder = new StringBuilder(s.length());
        Iterator<Character> iterator = new StringIterator(s);
        iterator.forEachRemaining(character -> {
            if (character != toRemove) builder.append(character);
        });
        return builder.toString();
    }
    public static String trimFront(String s){
        if (!s.startsWith(" ")){
            return s;
        }
        boolean stop = false;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' '){
                stop = true;
            }else if (stop){
                return s.substring(i);
            }
        }
        return s;
    }
    public static String makePleural(String s){
        return s + "'" + (s.endsWith("s") ? s + "" : "s");
    }
    public static String removeMention(String s){
        StringBuilder builder = new StringBuilder();
        new StringIterator(s).forEachRemaining(character -> {
            if (Character.isDigit(character)) builder.append(character);
        });
        return builder.toString();
    }
    public static int lengthOf(String[] args, int count){
        int l = 0;
        for (int i = 0; i < count; i++) {
            l += args[i].length();
        }
        return l;
    }
    /**
     * @param headers array containing the headers
     * @param table   array[n size] of array's[header size], containing the rows of the controllers
     * @param footer
     * @return a formatted controllers
     */
    public static String makeAsciiTable(List<String> headers, List<List<String>> table, List<String> footer) {
        StringBuilder sb = new StringBuilder();
        int padding = 1;
        int[] widths = new int[headers.size()];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = 0;
        }
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).length() > widths[i]) {
                widths[i] = headers.get(i).length();
                if (footer != null) {
                    widths[i] = Math.max(widths[i], footer.get(i).length());
                }
            }
        }
        for (List<String> row : table) {
            for (int i = 0; i < row.size(); i++) {
                String cell = row.get(i);
                if (cell.length() > widths[i]) {
                    widths[i] = cell.length();
                }
            }
        }
        sb.append("```").append("\n");
        String formatLine = "|";
        for (int width : widths) {
            formatLine += " %-" + width + "s |";
        }
        formatLine += "\n";
        sb.append(appendSeparatorLine("+", "+", "+", padding, widths));
        sb.append(String.format(formatLine, headers.toArray()));
        sb.append(appendSeparatorLine("+", "+", "+", padding, widths));
        for (List<String> row : table) {
            sb.append(String.format(formatLine, row.toArray()));
        }
        if (footer != null) {
            sb.append(appendSeparatorLine("+", "+", "+", padding, widths));
            sb.append(String.format(formatLine, footer.toArray()));
        }
        sb.append(appendSeparatorLine("+", "+", "+", padding, widths));
        sb.append("```");
        return sb.toString();
    }

    /**
     * helper function for makeAsciiTable
     *
     * @param left    character on the left
     * @param middle  character in the middle
     * @param right   character on the right
     * @param padding controllers cell padding
     * @param sizes   width of each cell
     * @return a filler row for the controllers
     */
    private static String appendSeparatorLine(String left, String middle, String right, int padding, int... sizes) {
        boolean first = true;
        StringBuilder ret = new StringBuilder();
        for (int size : sizes) {
            if (first) {
                first = false;
                ret.append(left).append(Strings.repeat("-", size + padding * 2));
            } else {
                ret.append(middle).append(Strings.repeat("-", size + padding * 2));
            }
        }
        return ret.append(right).append("\n").toString();
    }

    /**
     * @param items items in the controllers
     * @return formatted controllers
     */
    public static String makeTable(List<String> items) {
        return makeTable(items, 16, 4);
    }

    /**
     * Makes a controllers-like display of list of items
     *
     * @param items        items in the controllers
     * @param columnLength length of a column(filled up with whitespace)
     * @param columns      amount of columns
     * @return formatted controllers
     */
    public static String makeTable(List<String> items, int columnLength, int columns) {
        String ret = "```xl\n";
        int counter = 0;
        for (String item : items) {
            counter++;
            ret += String.format("%-" + columnLength + "s", item);
            if (counter % columns == 0) {
                ret += "\n";
            }
        }
        if (counter % columns != 0) {
            ret += "\n";
        }
        return ret + "```\n";
    }
}