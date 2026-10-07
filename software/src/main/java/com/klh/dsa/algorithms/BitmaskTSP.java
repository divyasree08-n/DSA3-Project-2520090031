package com.klh.dsa.algorithms;

import java.util.Arrays;

/** Held-Karp bitmask dynamic programming solution for the travelling salesperson. */
public final class BitmaskTSP {
    private static final int MAX_CITIES = 20;

    private BitmaskTSP() {
    }

    /** Returns the minimum cycle cost beginning and ending at city zero. */
    public static int solve(int[][] dist) {
        if (dist == null) {
            return 0;
        }
        int n = dist.length;
        if (n <= 1) {
            return 0;
        }
        if (n > MAX_CITIES) {
            throw new IllegalArgumentException("At most " + MAX_CITIES + " cities are supported");
        }
        for (int[] row : dist) {
            if (row == null || row.length != n) {
                throw new IllegalArgumentException("Distance matrix must be square");
            }
        }
        int states = 1 << n;
        long infinity = Long.MAX_VALUE / 4;
        long[][] dp = new long[states][n];
        for (long[] row : dp) {
            Arrays.fill(row, infinity);
        }
        dp[1][0] = 0;
        for (int mask = 1; mask < states; mask++) {
            if ((mask & 1) == 0) {
                continue;
            }
            for (int last = 0; last < n; last++) {
                if (dp[mask][last] == infinity) {
                    continue;
                }
                for (int next = 1; next < n; next++) {
                    if ((mask & (1 << next)) == 0) {
                        int nextMask = mask | (1 << next);
                        dp[nextMask][next] = Math.min(dp[nextMask][next],
                                dp[mask][last] + dist[last][next]);
                    }
                }
            }
        }
        long best = infinity;
        for (int last = 1; last < n; last++) {
            best = Math.min(best, dp[states - 1][last] + dist[last][0]);
        }
        return Math.toIntExact(best);
    }
}
