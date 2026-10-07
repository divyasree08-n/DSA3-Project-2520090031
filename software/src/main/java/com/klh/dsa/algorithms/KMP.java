package com.klh.dsa.algorithms;

import java.util.ArrayList;
import java.util.List;

/** Knuth-Morris-Pratt substring search. */
public final class KMP {
    private KMP() {
    }

    /** Builds the longest-proper-prefix/suffix table for a pattern. */
    public static int[] buildLPS(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return new int[0];
        }
        int[] lps = new int[pattern.length()];
        int length = 0;
        for (int i = 1; i < pattern.length();) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                lps[i++] = ++length;
            } else if (length > 0) {
                length = lps[length - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    /** Returns all starting indexes of pattern occurrences in text. */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null) {
            return matches;
        }
        if (pattern.isEmpty()) {
            for (int i = 0; i <= text.length(); i++) {
                matches.add(i);
            }
            return matches;
        }
        int[] lps = buildLPS(pattern);
        for (int i = 0, j = 0; i < text.length();) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    matches.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return matches;
    }

    /** Returns whether pattern occurs in text. */
    public static boolean contains(String text, String pattern) {
        return !search(text, pattern).isEmpty();
    }
}
