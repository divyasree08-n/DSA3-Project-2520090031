package com.klh.dsa.algorithms;

import java.util.ArrayList;
import java.util.List;

/** Rolling-hash substring search with exact match verification. */
public final class RabinKarp {
    private static final long BASE = 257L;
    private static final long MOD = 1_000_000_007L;

    private RabinKarp() {
    }

    /** Returns all starting indexes of pattern occurrences in text. */
    public static List<Integer> searchAll(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null) {
            return matches;
        }
        int n = text.length();
        int m = pattern.length();
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                matches.add(i);
            }
            return matches;
        }
        if (m > n) {
            return matches;
        }
        long highestPower = 1L;
        for (int i = 1; i < m; i++) {
            highestPower = highestPower * BASE % MOD;
        }
        long patternHash = 0L;
        long windowHash = 0L;
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * BASE + pattern.charAt(i)) % MOD;
            windowHash = (windowHash * BASE + text.charAt(i)) % MOD;
        }
        for (int start = 0; start <= n - m; start++) {
            if (patternHash == windowHash && text.regionMatches(start, pattern, 0, m)) {
                matches.add(start);
            }
            if (start < n - m) {
                windowHash = (windowHash - text.charAt(start) * highestPower % MOD + MOD) % MOD;
                windowHash = (windowHash * BASE + text.charAt(start + m)) % MOD;
            }
        }
        return matches;
    }
}
