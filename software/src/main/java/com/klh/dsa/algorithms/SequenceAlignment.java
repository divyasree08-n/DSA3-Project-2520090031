package com.klh.dsa.algorithms;

/** Global and local sequence alignment using dynamic programming. */
public final class SequenceAlignment {
    public static final int MATCH = 2;
    public static final int MISMATCH = -1;
    public static final int GAP = -1;

    private SequenceAlignment() {
    }

    /** Returns the Needleman-Wunsch global alignment score. */
    public static int needlemanWunsch(String first, String second) {
        String a = first == null ? "" : first;
        String b = second == null ? "" : second;
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 1; i <= a.length(); i++) {
            dp[i][0] = dp[i - 1][0] + GAP;
        }
        for (int j = 1; j <= b.length(); j++) {
            dp[0][j] = dp[0][j - 1] + GAP;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int substitution = a.charAt(i - 1) == b.charAt(j - 1) ? MATCH : MISMATCH;
                dp[i][j] = Math.max(dp[i - 1][j - 1] + substitution,
                        Math.max(dp[i - 1][j] + GAP, dp[i][j - 1] + GAP));
            }
        }
        return dp[a.length()][b.length()];
    }

    /** Returns the Smith-Waterman local alignment score. */
    public static int smithWaterman(String first, String second) {
        String a = first == null ? "" : first;
        String b = second == null ? "" : second;
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        int best = 0;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int substitution = a.charAt(i - 1) == b.charAt(j - 1) ? MATCH : MISMATCH;
                dp[i][j] = Math.max(0, Math.max(dp[i - 1][j - 1] + substitution,
                        Math.max(dp[i - 1][j] + GAP, dp[i][j - 1] + GAP)));
                best = Math.max(best, dp[i][j]);
            }
        }
        return best;
    }
}
