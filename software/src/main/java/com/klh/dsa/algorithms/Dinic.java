package com.klh.dsa.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Dinic maximum-flow algorithm with level graphs and blocking flows. */
public final class Dinic {
    /** Residual edge used by Dinic's algorithm. */
    public static final class Edge {
        /** Destination vertex. */
        public final int to;
        /** Index of the reverse residual edge. */
        public final int rev;
        /** Residual capacity. */
        public int cap;

        private Edge(int to, int rev, int cap) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
        }
    }

    private final List<Edge>[] graph;
    private final int[] level;
    private final int[] nextEdge;

    /** Creates a flow network with n vertices indexed from zero. */
    @SuppressWarnings("unchecked")
    public Dinic(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative");
        }
        graph = (List<Edge>[]) new List<?>[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }
        level = new int[n];
        nextEdge = new int[n];
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
        while (buildLevels(source, sink)) {
            Arrays.fill(nextEdge, 0);
            int flow;
            while ((flow = sendFlow(source, sink, Integer.MAX_VALUE)) > 0) {
                total = Math.addExact(total, flow);
            }
        }
        return total;
    }

    private boolean buildLevels(int source, int sink) {
        Arrays.fill(level, -1);
        int[] queue = new int[graph.length];
        int head = 0;
        int tail = 0;
        queue[tail++] = source;
        level[source] = 0;
        while (head < tail) {
            int current = queue[head++];
            for (Edge edge : graph[current]) {
                if (edge.cap > 0 && level[edge.to] == -1) {
                    level[edge.to] = level[current] + 1;
                    queue[tail++] = edge.to;
                }
            }
        }
        return level[sink] != -1;
    }

    private int sendFlow(int current, int sink, int available) {
        if (current == sink) {
            return available;
        }
        while (nextEdge[current] < graph[current].size()) {
            Edge edge = graph[current].get(nextEdge[current]);
            if (edge.cap > 0 && level[edge.to] == level[current] + 1) {
                int pushed = sendFlow(edge.to, sink, Math.min(available, edge.cap));
                if (pushed > 0) {
                    edge.cap -= pushed;
                    graph[edge.to].get(edge.rev).cap += pushed;
                    return pushed;
                }
            }
            nextEdge[current]++;
        }
        return 0;
    }

    private void checkVertex(int vertex) {
        if (vertex < 0 || vertex >= graph.length) {
            throw new IllegalArgumentException("Vertex is outside the network");
        }
    }
}
