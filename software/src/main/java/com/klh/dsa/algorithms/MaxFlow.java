package com.klh.dsa.algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/** Edmonds-Karp maximum flow using breadth-first augmenting paths. */
public final class MaxFlow {
    private static final class Edge {
        private final int to;
        private final int reverseIndex;
        private int capacity;

        private Edge(int to, int reverseIndex, int capacity) {
            this.to = to;
            this.reverseIndex = reverseIndex;
            this.capacity = capacity;
        }
    }

    private final List<Edge>[] graph;

    /** Creates a flow network with n vertices indexed from zero. */
    @SuppressWarnings("unchecked")
    public MaxFlow(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative");
        }
        graph = (List<Edge>[]) new List<?>[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }
    }

    /** Adds a directed edge with the specified non-negative capacity. */
    public void addEdge(int from, int to, int capacity) {
        checkVertex(from);
        checkVertex(to);
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }
        if (from == to) {
            return;
        }
        Edge forward = new Edge(to, graph[to].size(), capacity);
        Edge reverse = new Edge(from, graph[from].size(), 0);
        graph[from].add(forward);
        graph[to].add(reverse);
    }

    /** Computes the maximum flow from source to sink. */
    public int maxFlow(int source, int sink) {
        checkVertex(source);
        checkVertex(sink);
        if (source == sink) {
            return 0;
        }
        int total = 0;
        int[] parentVertex = new int[graph.length];
        int[] parentEdge = new int[graph.length];
        while (findPath(source, sink, parentVertex, parentEdge)) {
            int flow = Integer.MAX_VALUE;
            for (int vertex = sink; vertex != source; vertex = parentVertex[vertex]) {
                flow = Math.min(flow, graph[parentVertex[vertex]].get(parentEdge[vertex]).capacity);
            }
            for (int vertex = sink; vertex != source; vertex = parentVertex[vertex]) {
                Edge edge = graph[parentVertex[vertex]].get(parentEdge[vertex]);
                edge.capacity -= flow;
                graph[vertex].get(edge.reverseIndex).capacity += flow;
            }
            total = Math.addExact(total, flow);
        }
        return total;
    }

    private boolean findPath(int source, int sink, int[] parentVertex, int[] parentEdge) {
        Arrays.fill(parentVertex, -1);
        parentVertex[source] = source;
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(source);
        while (!queue.isEmpty() && parentVertex[sink] == -1) {
            int current = queue.remove();
            for (int i = 0; i < graph[current].size(); i++) {
                Edge edge = graph[current].get(i);
                if (edge.capacity > 0 && parentVertex[edge.to] == -1) {
                    parentVertex[edge.to] = current;
                    parentEdge[edge.to] = i;
                    queue.add(edge.to);
                }
            }
        }
        return parentVertex[sink] != -1;
    }

    private void checkVertex(int vertex) {
        if (vertex < 0 || vertex >= graph.length) {
            throw new IllegalArgumentException("Vertex is outside the network");
        }
    }
}
