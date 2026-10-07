package com.klh.dsa.algorithms;

/** One-dimensional dynamic programming solution to 0/1 knapsack. */
public final class KnapsackDP {
    private KnapsackDP() {
    }

    /** Returns the maximum value obtainable without exceeding capacity. */
    public static int solve(int[] weights, int[] values, int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }
        if (weights == null || values == null) {
            return 0;
        }
        if (weights.length != values.length) {
            throw new IllegalArgumentException("Weights and values must have equal lengths");
        }
        int[] best = new int[capacity + 1];
        for (int item = 0; item < weights.length; item++) {
            if (weights[item] < 0 || values[item] < 0) {
                throw new IllegalArgumentException("Weights and values cannot be negative");
            }
            for (int currentCapacity = capacity; currentCapacity >= weights[item]; currentCapacity--) {
                best[currentCapacity] = Math.max(best[currentCapacity],
                        best[currentCapacity - weights[item]] + values[item]);
            }
        }
        return best[capacity];
    }
}
