package com.klh.dsa.algorithms;

import java.util.Arrays;

/** Sequential and Hillis-Steele parallel-style prefix sum implementations. */
public final class ParallelPrefixSum {
    private ParallelPrefixSum() {
    }

    /** Returns the inclusive prefix sum using a sequential scan. */
    public static int[] sequential(int[] values) {
        if (values == null) {
            return new int[0];
        }
        int[] result = new int[values.length];
        int sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum = Math.addExact(sum, values[i]);
            result[i] = sum;
        }
        return result;
    }

    /** Returns the inclusive prefix sum using Hillis-Steele scan stages. */
    public static int[] parallel(int[] values) {
        if (values == null) {
            return new int[0];
        }
        int[] result = Arrays.copyOf(values, values.length);
        for (int offset = 1; offset < result.length; offset *= 2) {
            int[] previous = Arrays.copyOf(result, result.length);
            for (int i = offset; i < result.length; i++) {
                result[i] = Math.addExact(previous[i], previous[i - offset]);
            }
            if (offset > result.length / 2) {
                break;
            }
        }
        return result;
    }

    /** Benchmarks both prefix-sum methods on deterministic input. */
    public static void benchmark(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Input length cannot be negative");
        }
        int[] values = new int[n];
        Arrays.fill(values, 1);
        long start = System.nanoTime();
        int[] sequentialResult = sequential(values);
        long sequentialTime = System.nanoTime() - start;
        start = System.nanoTime();
        int[] parallelResult = parallel(values);
        long parallelTime = System.nanoTime() - start;
        System.out.printf("Prefix sum benchmark (n=%d): sequential=%d ns, Hillis-Steele=%d ns, verified=%b%n",
                n, sequentialTime, parallelTime, Arrays.equals(sequentialResult, parallelResult));
    }
}
