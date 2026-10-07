package com.klh.dsa.algorithms;

/** Levenshtein edit distance using linear auxiliary space. */
public final class EditDistance {
    private EditDistance() {
    }

    /** Computes the minimum insertions, deletions, and substitutions. */
    public static int compute(String first, String second) {
        if (first == null) {
            first = "";
        }
        if (second == null) {
            second = "";
        }
        if (first.length() < second.length()) {
            String temporary = first;
            first = second;
            second = temporary;
        }
        int[] previous = new int[second.length() + 1];
        int[] current = new int[second.length() + 1];
        for (int j = 0; j <= second.length(); j++) {
            previous[j] = j;
        }
        for (int i = 1; i <= first.length(); i++) {
            current[0] = i;
            for (int j = 1; j <= second.length(); j++) {
                int substitution = previous[j - 1]
                        + (first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1);
                current[j] = Math.min(substitution,
                        Math.min(previous[j] + 1, current[j - 1] + 1));
            }
            int[] temporary = previous;
            previous = current;
            current = temporary;
        }
        return previous[second.length()];
    }

    /** Returns normalized edit similarity in the inclusive range [0, 1]. */
    public static double similarity(String first, String second) {
        String left = first == null ? "" : first;
        String right = second == null ? "" : second;
        int longest = Math.max(left.length(), right.length());
        return longest == 0 ? 1.0 : 1.0 - (double) compute(left, right) / longest;
    }
}
