package com.klh.dsa.algorithms;

/** Matrix-chain multiplication using interval dynamic programming. */
public final class IntervalDP {
    private IntervalDP() {
    }

    /** Returns the minimum scalar multiplications for the dimension chain. */
    public static int matrixChain(int[] dimensions) {
        if (dimensions == null || dimensions.length < 2) {
            return 0;
        }
        for (int dimension : dimensions) {
            if (dimension <= 0) {
                throw new IllegalArgumentException("Matrix dimensions must be positive");
            }
        }
        int matrices = dimensions.length - 1;
        long[][] dp = new long[matrices][matrices];
        for (int length = 2; length <= matrices; length++) {
            for (int left = 0; left + length <= matrices; left++) {
                int right = left + length - 1;
                dp[left][right] = Long.MAX_VALUE;
                for (int split = left; split < right; split++) {
                    long cost = dp[left][split] + dp[split + 1][right]
                            + (long) dimensions[left] * dimensions[split + 1] * dimensions[right + 1];
                    dp[left][right] = Math.min(dp[left][right], cost);
                }
            }
        }
        return Math.toIntExact(dp[0][matrices - 1]);
    }
}
