package com.klh.dsa.algorithms;

import java.util.ArrayList;
import java.util.List;

/** Linear-time Z-function and pattern search. */
public final class ZFunction {
    private ZFunction() {
    }

    /** Computes the Z-array, where each value is the matching prefix length. */
    public static int[] compute(String value) {
        if (value == null) {
            return new int[0];
        }
        int n = value.length();
        int[] z = new int[n];
        int left = 0;
        int right = 0;
        for (int i = 1; i < n; i++) {
            if (i <= right) {
                z[i] = Math.min(right - i + 1, z[i - left]);
            }
            while (i + z[i] < n && value.charAt(z[i]) == value.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }
        }
        return z;
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
        String combined = pattern + '\0' + text;
        int[] z = compute(combined);
        for (int i = pattern.length() + 1; i < combined.length(); i++) {
            if (z[i] >= pattern.length()) {
                matches.add(i - pattern.length() - 1);
            }
        }
        return matches;
    }
}
