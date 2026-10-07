package com.klh.dsa.algorithms;

import java.util.Arrays;

/** Suffix-array construction, LCP computation, and longest common substring. */
public final class SuffixArray {
    private SuffixArray() {
    }

    /** Builds the suffix array in O(n log^2 n) time. */
    public static int[] build(String value) {
        if (value == null || value.isEmpty()) {
            return new int[0];
        }
        int n = value.length();
        Integer[] order = new Integer[n];
        int[] rank = new int[n];
        int[] nextRank = new int[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
            rank[i] = value.charAt(i);
        }
        for (int width = 1; width < n; width *= 2) {
            final int currentWidth = width;
            final int[] currentRank = rank;
            Arrays.sort(order, (left, right) -> {
                int first = Integer.compare(currentRank[left], currentRank[right]);
                if (first != 0) {
                    return first;
                }
                int leftSecond = left + currentWidth < n ? currentRank[left + currentWidth] : -1;
                int rightSecond = right + currentWidth < n ? currentRank[right + currentWidth] : -1;
                return Integer.compare(leftSecond, rightSecond);
            });
            nextRank[order[0]] = 0;
            for (int i = 1; i < n; i++) {
                int previous = order[i - 1];
                int current = order[i];
                boolean differs = rank[previous] != rank[current]
                        || (previous + width < n ? rank[previous + width] : -1)
                        != (current + width < n ? rank[current + width] : -1);
                nextRank[current] = nextRank[previous] + (differs ? 1 : 0);
            }
            int[] temporary = rank;
            rank = nextRank;
            nextRank = temporary;
            if (rank[order[n - 1]] == n - 1 || width > n / 2) {
                break;
            }
        }
        int[] suffixArray = new int[n];
        for (int i = 0; i < n; i++) {
            suffixArray[i] = order[i];
        }
        return suffixArray;
    }

    /** Computes the Kasai LCP array, with lcp[i] for sa[i] and sa[i - 1]. */
    public static int[] kasaiLCP(String value, int[] suffixArray) {
        if (value == null || suffixArray == null || suffixArray.length != value.length()) {
            return new int[0];
        }
        int n = value.length();
        int[] lcp = new int[n];
        int[] inverse = new int[n];
        for (int i = 0; i < n; i++) {
            int suffix = suffixArray[i];
            if (suffix < 0 || suffix >= n) {
                return new int[0];
            }
            inverse[suffix] = i;
        }
        int common = 0;
        for (int i = 0; i < n; i++) {
            int position = inverse[i];
            if (position == 0) {
                continue;
            }
            int previous = suffixArray[position - 1];
            while (i + common < n && previous + common < n
                    && value.charAt(i + common) == value.charAt(previous + common)) {
                common++;
            }
            lcp[position] = common;
            if (common > 0) {
                common--;
            }
        }
        return lcp;
    }

    /** Returns the length of the longest substring common to both strings. */
    public static int longestCommonSubstring(String first, String second) {
        if (first == null || second == null || first.isEmpty() || second.isEmpty()) {
            return 0;
        }
        String combined = first + '\0' + second;
        int[] suffixArray = build(combined);
        int[] lcp = kasaiLCP(combined, suffixArray);
        int separator = first.length();
        int longest = 0;
        for (int i = 1; i < suffixArray.length; i++) {
            int left = suffixArray[i - 1];
            int right = suffixArray[i];
            boolean leftInFirst = left < separator;
            boolean rightInFirst = right < separator;
            if (leftInFirst != rightInFirst) {
                int leftLimit = leftInFirst ? separator - left : combined.length() - left;
                int rightLimit = rightInFirst ? separator - right : combined.length() - right;
                longest = Math.max(longest, Math.min(lcp[i], Math.min(leftLimit, rightLimit)));
            }
        }
        return longest;
    }
}
