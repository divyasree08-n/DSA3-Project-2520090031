package com.klh.dsa.algorithms;

import java.util.Arrays;
import java.util.Comparator;

/** Greedy interval scheduling that selects the maximum number of interviews. */
public final class SchedulingApprox {
    private SchedulingApprox() {
    }

    /** Returns the maximum number of non-overlapping intervals. */
    public static int scheduleInterviews(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }
        int[][] ordered = new int[intervals.length][2];
        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i] == null || intervals[i].length < 2) {
                throw new IllegalArgumentException("Each interval must have a start and end");
            }
            if (intervals[i][0] > intervals[i][1]) {
                throw new IllegalArgumentException("Interval start cannot exceed its end");
            }
            ordered[i] = Arrays.copyOf(intervals[i], 2);
        }
        Arrays.sort(ordered, Comparator.comparingInt((int[] interval) -> interval[1])
                .thenComparingInt(interval -> interval[0]));
        int count = 0;
        int lastEnd = Integer.MIN_VALUE;
        for (int[] interval : ordered) {
            if (count == 0 || interval[0] >= lastEnd) {
                count++;
                lastEnd = interval[1];
            }
        }
        return count;
    }
}
