package com.klh.dsa;

import com.klh.dsa.algorithms.AhoCorasick;
import com.klh.dsa.algorithms.BitmaskTSP;
import com.klh.dsa.algorithms.Dinic;
import com.klh.dsa.algorithms.EditDistance;
import com.klh.dsa.algorithms.HungarianAlgorithm;
import com.klh.dsa.algorithms.IntervalDP;
import com.klh.dsa.algorithms.KMP;
import com.klh.dsa.algorithms.KnapsackDP;
import com.klh.dsa.algorithms.MaxFlow;
import com.klh.dsa.algorithms.MillerRabin;
import com.klh.dsa.algorithms.ParallelPrefixSum;
import com.klh.dsa.algorithms.RabinKarp;
import com.klh.dsa.algorithms.ReservoirSampling;
import com.klh.dsa.algorithms.SchedulingApprox;
import com.klh.dsa.algorithms.SequenceAlignment;
import com.klh.dsa.algorithms.SuffixArray;
import com.klh.dsa.algorithms.VertexCoverApprox;
import com.klh.dsa.algorithms.ZFunction;
import com.klh.dsa.core.ProblemClassifier;
import com.klh.dsa.matcher.MatchingEngine;
import com.klh.dsa.model.Candidate;
import com.klh.dsa.model.Job;
import com.klh.dsa.model.MatchResult;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Runnable demonstration of the DSA-3 resume/job matching project. */
public final class Main {
    private Main() {
    }

    /** Runs each course-outcome demonstration and the matching application example. */
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println(" Resume-Job Matching & Talent Marketplace Engine");
        System.out.println("DSA-3 | 25CS2103E | Java " + System.getProperty("java.version"));
        System.out.println("==========================================================");
        demonstrateCO1_Classification();
        demonstrateCO2_StringAlgorithms();
        demonstrateCO3_DynamicProgramming();
        demonstrateCO4_NetworkFlow();
        demonstrateCO5_NPApproximation();
        demonstrateCO6_RandomizedParallel();
        runFullMatchingDemo();
        System.out.println("\nALL 6 COURSE OUTCOMES DEMONSTRATED");
    }

    /** Compatibility entry point for the first course outcome. */
    public static void demonstrateCO1() {
        demonstrateCO1_Classification();
    }

    /** Compatibility entry point for the second course outcome. */
    public static void demonstrateCO2() {
        demonstrateCO2_StringAlgorithms();
    }

    /** Compatibility entry point for the third course outcome. */
    public static void demonstrateCO3() {
        demonstrateCO3_DynamicProgramming();
    }

    /** Compatibility entry point for the fourth course outcome. */
    public static void demonstrateCO4() {
        demonstrateCO4_NetworkFlow();
    }

    /** Compatibility entry point for the fifth course outcome. */
    public static void demonstrateCO5() {
        demonstrateCO5_NPApproximation();
    }

    /** Compatibility entry point for the sixth course outcome. */
    public static void demonstrateCO6() {
        demonstrateCO6_RandomizedParallel();
    }

    /** Demonstrates natural-language query classification. */
    public static void demonstrateCO1_Classification() {
        section("CO1 - Problem Classification");
        List<String> queries = List.of("Find exact Java keyword matches", "Fuzzy Python skill match",
                "Search multiple resume patterns", "Compare document similarity",
                "Assign candidates to jobs", "Schedule interviews", "Test prime hash values");
        for (String query : queries) {
            ProblemClassifier.QueryType type = ProblemClassifier.classify(query);
            System.out.printf("%-34s -> %-27s : %s%n", query, type,
                    ProblemClassifier.algorithmFor(type));
        }
    }

    /** Demonstrates hand-coded exact and multi-pattern string algorithms. */
    public static void demonstrateCO2_StringAlgorithms() {
        section("CO2 - String Algorithms");
        String text = "java and python developers use java";
        String pattern = "java";
        System.out.println("KMP matches: " + KMP.search(text, pattern));
        System.out.println("Z-function matches: " + ZFunction.search(text, pattern));
        System.out.println("Rabin-Karp matches: " + RabinKarp.searchAll(text, pattern));
        AhoCorasick aho = new AhoCorasick();
        aho.addPattern("java");
        aho.addPattern("python");
        aho.addPattern("developer");
        Map<String, List<Integer>> matches = aho.search(text);
        System.out.println("Aho-Corasick matches: " + matches);
        String suffixInput = "banana";
        System.out.println("Suffix array of \"" + suffixInput + "\": "
                + Arrays.toString(SuffixArray.build(suffixInput)));
        System.out.println("Longest common substring (banana/ananas): "
                + SuffixArray.longestCommonSubstring("banana", "ananas"));
    }

    /** Demonstrates dynamic programming and sequence optimization. */
    public static void demonstrateCO3_DynamicProgramming() {
        section("CO3 - Dynamic Programming");
        System.out.println("Edit distance (kitten/sitting): " + EditDistance.compute("kitten", "sitting"));
        System.out.println("Edit similarity (Java/Java): " + EditDistance.similarity("Java", "Java"));
        System.out.println("Needleman-Wunsch (ACGT/AGT): "
                + SequenceAlignment.needlemanWunsch("ACGT", "AGT"));
        System.out.println("Smith-Waterman (GGACGT/ACG): "
                + SequenceAlignment.smithWaterman("GGACGT", "ACG"));
        System.out.println("Matrix-chain minimum cost: "
                + IntervalDP.matrixChain(new int[]{40, 20, 30, 10, 30}));
        int[][] distances = {{0, 10, 15, 20}, {10, 0, 35, 25},
                {15, 35, 0, 30}, {20, 25, 30, 0}};
        System.out.println("Bitmask TSP minimum cycle: " + BitmaskTSP.solve(distances));
    }

    /** Demonstrates maximum flow and weighted assignment algorithms. */
    public static void demonstrateCO4_NetworkFlow() {
        section("CO4 - Network Flow and Assignment");
        MaxFlow edmondsKarp = new MaxFlow(6);
        Dinic dinic = new Dinic(6);
        int[][] edges = {{0, 1, 16}, {0, 2, 13}, {1, 2, 10}, {2, 1, 4},
                {1, 3, 12}, {3, 2, 9}, {2, 4, 14}, {4, 3, 7}, {3, 5, 20}, {4, 5, 4}};
        for (int[] edge : edges) {
            edmondsKarp.addEdge(edge[0], edge[1], edge[2]);
            dinic.addEdge(edge[0], edge[1], edge[2]);
        }
        System.out.println("Edmonds-Karp max flow: " + edmondsKarp.maxFlow(0, 5));
        System.out.println("Dinic max flow: " + dinic.maxFlow(0, 5));
        double[][] assignmentCosts = {{4, 1, 3}, {2, 0, 5}, {3, 2, 2}};
        System.out.println("Hungarian assignment (column per row): "
                + Arrays.toString(HungarianAlgorithm.solve(assignmentCosts)));
    }

    /** Demonstrates approximation and greedy optimization algorithms. */
    public static void demonstrateCO5_NPApproximation() {
        section("CO5 - NP Approximation and Greedy Algorithms");
        int[][] edges = {{0, 1}, {1, 2}, {2, 3}, {3, 0}};
        System.out.println("2-approximate vertex cover: " + VertexCoverApprox.approximate(4, edges));
        System.out.println("0/1 knapsack maximum value: "
                + KnapsackDP.solve(new int[]{2, 3, 4, 5}, new int[]{3, 4, 5, 6}, 5));
        int[][] interviews = {{1, 3}, {2, 5}, {4, 7}, {6, 9}, {8, 10}};
        System.out.println("Maximum non-overlapping interviews: "
                + SchedulingApprox.scheduleInterviews(interviews));
    }

    /** Demonstrates primality testing, reservoir sampling, and prefix sums. */
    public static void demonstrateCO6_RandomizedParallel() {
        section("CO6 - Randomized and Parallel Algorithms");
        System.out.println("Miller-Rabin: 104729 prime = " + MillerRabin.isPrime(104729L, 7)
                + ", 104730 prime = " + MillerRabin.isPrime(104730L, 7));
        System.out.println("Reservoir sample of 3: "
                + ReservoirSampling.sample(Arrays.asList(1, 2, 3, 4, 5, 6).iterator(), 3));
        int[] values = {1, 2, 3, 4, 5};
        System.out.println("Sequential prefix sum: " + Arrays.toString(ParallelPrefixSum.sequential(values)));
        System.out.println("Hillis-Steele prefix sum: " + Arrays.toString(ParallelPrefixSum.parallel(values)));
        ParallelPrefixSum.benchmark(1_000);
    }

    /** Builds sample marketplace data and prints the highest-scoring matches. */
    public static void runFullMatchingDemo() {
        section("Full Resume-to-Job Matching Demo");
        Candidate alice = new Candidate("C1", "Alice", "Java Spring developer with SQL and AWS");
        alice.addSkill("Java");
        alice.addSkill("Spring");
        alice.addSkill("SQL");
        alice.addSkill("AWS");
        for (String keyword : List.of("backend", "microservices", "cloud")) {
            alice.addKeyword(keyword);
        }

        Candidate bob = new Candidate("C2", "Bob", "Python machine learning engineer with SQL");
        bob.addSkill("Python");
        bob.addSkill("Machine Learning");
        bob.addSkill("SQL");
        for (String keyword : List.of("data", "machine learning", "analytics")) {
            bob.addKeyword(keyword);
        }

        Candidate carol = new Candidate("C3", "Carol", "React JavaScript and TypeScript frontend engineer");
        carol.addSkill("React");
        carol.addSkill("JavaScript");
        carol.addSkill("TypeScript");
        for (String keyword : List.of("frontend", "ui", "web")) {
            carol.addKeyword(keyword);
        }

        Job backend = new Job("J1", "Java Backend Engineer", "Build backend microservices in Java and Spring");
        backend.addRequiredSkill("Java");
        backend.addRequiredSkill("Spring");
        backend.addRequiredSkill("SQL");
        for (String keyword : List.of("backend", "microservices", "cloud")) {
            backend.addKeyword(keyword);
        }

        Job data = new Job("J2", "Data Scientist", "Develop Python machine learning data products");
        data.addRequiredSkill("Python");
        data.addRequiredSkill("Machine Learning");
        data.addRequiredSkill("SQL");
        for (String keyword : List.of("data", "machine learning", "analytics")) {
            data.addKeyword(keyword);
        }

        Job frontend = new Job("J3", "Frontend Developer", "Create React JavaScript web interfaces");
        frontend.addRequiredSkill("React");
        frontend.addRequiredSkill("JavaScript");
        frontend.addRequiredSkill("TypeScript");
        for (String keyword : List.of("frontend", "ui", "web")) {
            frontend.addKeyword(keyword);
        }

        MatchingEngine engine = new MatchingEngine(List.of(alice, bob, carol), List.of(backend, data, frontend));
        for (MatchResult result : engine.rankAll()) {
            System.out.println("  " + result);
        }
    }

    private static void section(String title) {
        System.out.println("\n--- " + title + " ---");
    }
}
