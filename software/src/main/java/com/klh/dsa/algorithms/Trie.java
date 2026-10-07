package com.klh.dsa.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Prefix tree for keyword lookup and autocomplete. */
public final class Trie {
    private static final class Node {
        private final Map<Character, Node> children = new TreeMap<>();
        private boolean terminal;
    }

    private final Node root = new Node();

    /** Inserts a word, including the empty word when supplied. */
    public void insert(String word) {
        if (word == null) {
            return;
        }
        Node current = root;
        for (int i = 0; i < word.length(); i++) {
            current = current.children.computeIfAbsent(word.charAt(i), ignored -> new Node());
        }
        current.terminal = true;
    }

    /** Returns whether the exact word has been inserted. */
    public boolean search(String word) {
        Node node = find(word);
        return node != null && node.terminal;
    }

    /** Returns whether at least one word has the supplied prefix. */
    public boolean startsWith(String prefix) {
        return find(prefix) != null;
    }

    /** Returns inserted words beginning with prefix in lexicographic order. */
    public List<String> autocomplete(String prefix) {
        Node node = find(prefix);
        if (node == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        collect(node, new StringBuilder(prefix), result);
        return result;
    }

    private Node find(String value) {
        if (value == null) {
            return null;
        }
        Node current = root;
        for (int i = 0; i < value.length(); i++) {
            current = current.children.get(value.charAt(i));
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private void collect(Node node, StringBuilder prefix, List<String> result) {
        if (node.terminal) {
            result.add(prefix.toString());
        }
        for (Map.Entry<Character, Node> entry : node.children.entrySet()) {
            prefix.append(entry.getKey());
            collect(entry.getValue(), prefix, result);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }
}
