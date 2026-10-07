package com.klh.dsa.algorithms;

import java.util.HashSet;
import java.util.Set;

/** Two-approximation for the minimum vertex cover via maximal matching. */
public final class VertexCoverApprox {
    private VertexCoverApprox() {
    }

    /** Returns the endpoints of a greedily constructed maximal matching. */
    public static Set<Integer> approximate(int n, int[][] edges) {
        if (n < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative");
        }
        Set<Integer> cover = new HashSet<>();
        boolean[] matched = new boolean[n];
        if (edges == null) {
            return cover;
        }
        for (int[] edge : edges) {
            if (edge == null || edge.length < 2) {
                continue;
            }
            int first = edge[0];
            int second = edge[1];
            if (first < 0 || first >= n || second < 0 || second >= n) {
                throw new IllegalArgumentException("Edge endpoint is outside the graph");
            }
            if (first != second && !matched[first] && !matched[second]) {
                matched[first] = true;
                matched[second] = true;
                cover.add(first);
                cover.add(second);
            }
        }
        return cover;
    }
}
