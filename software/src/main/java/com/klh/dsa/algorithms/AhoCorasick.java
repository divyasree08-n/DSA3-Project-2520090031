package com.klh.dsa.algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/** Trie-based multi-pattern search with failure links. */
public final class AhoCorasick {
    private final Node root = new Node();
    private boolean built;

    private static final class Node {
        private final Map<Character, Node> children = new HashMap<>();
        private final List<String> terminalPatterns = new ArrayList<>();
        private final List<String> outputs = new ArrayList<>();
        private Node failure;
    }

    /** Adds a non-empty pattern to the automaton. */
    public void addPattern(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return;
        }
        Node current = root;
        for (int i = 0; i < pattern.length(); i++) {
            current = current.children.computeIfAbsent(pattern.charAt(i), ignored -> new Node());
        }
        if (!current.terminalPatterns.contains(pattern)) {
            current.terminalPatterns.add(pattern);
        }
        built = false;
    }

    /** Computes failure links for all trie nodes. */
    public void buildFailureLinks() {
        Queue<Node> queue = new ArrayDeque<>();
        root.failure = root;
        for (Node child : root.children.values()) {
            child.failure = root;
            child.outputs.clear();
            child.outputs.addAll(child.terminalPatterns);
            queue.add(child);
        }
        while (!queue.isEmpty()) {
            Node parent = queue.remove();
            for (Map.Entry<Character, Node> entry : parent.children.entrySet()) {
                char character = entry.getKey();
                Node child = entry.getValue();
                Node fallback = parent.failure;
                while (fallback != root && !fallback.children.containsKey(character)) {
                    fallback = fallback.failure;
                }
                Node transition = fallback.children.get(character);
                child.failure = transition != null && transition != child ? transition : root;
                child.outputs.clear();
                child.outputs.addAll(child.terminalPatterns);
                child.outputs.addAll(child.failure.outputs);
                queue.add(child);
            }
        }
        built = true;
    }

    /** Maps each registered pattern to the indexes where it occurs. */
    public Map<String, List<Integer>> search(String text) {
        Map<String, List<Integer>> matches = new HashMap<>();
        if (text == null) {
            return matches;
        }
        if (!built) {
            buildFailureLinks();
        }
        Node current = root;
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            while (current != root && !current.children.containsKey(character)) {
                current = current.failure;
            }
            current = current.children.getOrDefault(character, root);
            for (String pattern : current.outputs) {
                matches.computeIfAbsent(pattern, ignored -> new ArrayList<>())
                        .add(i - pattern.length() + 1);
            }
        }
        return matches;
    }
}
