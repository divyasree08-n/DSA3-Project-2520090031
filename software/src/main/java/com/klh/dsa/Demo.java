package com.klh.dsa;

import com.klh.dsa.algorithms.EditDistance;
import com.klh.dsa.algorithms.HungarianAlgorithm;
import com.klh.dsa.algorithms.KMP;
import com.klh.dsa.algorithms.KnapsackDP;
import com.klh.dsa.algorithms.MillerRabin;
import com.klh.dsa.algorithms.RabinKarp;
import com.klh.dsa.algorithms.ZFunction;
import com.klh.dsa.core.ProblemClassifier;
import com.klh.dsa.matcher.MatchingEngine;
import com.klh.dsa.model.Candidate;
import com.klh.dsa.model.Job;
import com.klh.dsa.model.MatchResult;
import com.klh.dsa.nlp.TextProcessor;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/** Interactive console demonstrations for the Resume-Job Matching project. */
public final class Demo {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance(Locale.US);

    private Demo() {
    }

    /** Starts the interactive menu and runs the selected demonstration. */
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  Resume-Job Matching — Interactive Demo");
        System.out.println("  DSA-3 | 25CS2103E | Team 1, Section 12");
        System.out.println("============================================================");

        boolean running = true;
        while (running) {
            printMenu();
            String selection = readLine("Select an option: ");
            if (selection == null) {
                break;
            }

            switch (selection.trim()) {
                case "0" -> running = false;
                case "1" -> {
                    demoCO1_Classification();
                    pause();
                }
                case "2" -> {
                    demoCO2_StringSearch();
                    pause();
                }
                case "3" -> {
                    demoCO3_EditDistance();
                    pause();
                }
                case "4" -> {
                    demoCO4_Assignment();
                    pause();
                }
                case "5" -> {
                    demoCO5_Knapsack();
                    pause();
                }
                case "6" -> {
                    demoCO6_Primality();
                    pause();
                }
                case "7" -> {
                    demoFullMatching();
                    pause();
                }
                default -> {
                    System.out.println("Invalid option. Please choose a number from 0 to 7.");
                    pause();
                }
            }
        }

        System.out.println("Thanks for exploring the Resume-Job Matching demo. Goodbye!");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1  ->  CO1: Classify a query (which algorithm?)");
        System.out.println("2  ->  CO2: Search a pattern in text (KMP/RK/Z)");
        System.out.println("3  ->  CO3: Edit distance between two strings");
        System.out.println("4  ->  CO4: Optimal candidate-job assignment");
        System.out.println("5  ->  CO5: 0/1 Knapsack (budget-constrained hiring)");
        System.out.println("6  ->  CO6: Miller-Rabin primality test");
        System.out.println("7  ->  FULL: Match resumes to jobs");
        System.out.println("0  ->  Exit");
    }

    private static void demoCO1_Classification() {
        String query = readNonEmptyLine("Enter your query: ");
        if (query == null) {
            return;
        }

        ProblemClassifier.QueryType type = ProblemClassifier.classify(query);
        String algorithm = ProblemClassifier.algorithmFor(type);
        printBox(
                "Query           : " + query,
                "Classified Type : " + type,
                "Best Algorithm  : " + algorithm);
    }

    private static void demoCO2_StringSearch() {
        String text = readLine("Enter text: ");
        if (text == null) {
            return;
        }
        String pattern = readLine("Enter pattern: ");
        if (pattern == null) {
            return;
        }
        if (text.isBlank() || pattern.isBlank()) {
            System.out.println("Text and pattern must both be non-empty.");
            return;
        }

        long start = System.nanoTime();
        List<Integer> kmpMatches = KMP.search(text, pattern);
        long kmpTime = System.nanoTime() - start;

        start = System.nanoTime();
        List<Integer> zMatches = ZFunction.search(text, pattern);
        long zTime = System.nanoTime() - start;

        start = System.nanoTime();
        List<Integer> rabinKarpMatches = RabinKarp.searchAll(text, pattern);
        long rabinKarpTime = System.nanoTime() - start;

        printBox(
                "KMP first match       : " + firstMatch(kmpMatches),
                "KMP time              : " + kmpTime + " ns",
                "Z-Function first match: " + firstMatch(zMatches),
                "Z-Function time       : " + zTime + " ns",
                "Rabin-Karp matches    : " + rabinKarpMatches,
                "Rabin-Karp time       : " + rabinKarpTime + " ns");
        System.out.println("Verdict: Pattern " + (rabinKarpMatches.isEmpty() ? "NOT FOUND" : "FOUND"));
    }

    private static void demoCO3_EditDistance() {
        String first = readNonEmptyLine("Enter string A: ");
        if (first == null) {
            return;
        }
        String second = readNonEmptyLine("Enter string B: ");
        if (second == null) {
            return;
        }

        int distance = EditDistance.compute(first, second);
        double similarity = EditDistance.similarity(first, second);
        printBox(
                "String A      : " + first,
                "String B      : " + second,
                "Edit Distance : " + distance,
                "Similarity    : %.4f (%.2f%%)".formatted(similarity, similarity * 100.0));

        if (similarity > 0.8) {
            System.out.println("Verdict: Very similar — likely a typo.");
        } else if (similarity > 0.5) {
            System.out.println("Verdict: Moderately similar.");
        } else {
            System.out.println("Verdict: Different strings.");
        }
    }

    private static void demoCO4_Assignment() {
        Integer count = readIntegerInRange("Number of candidates (1..10): ", 1, 10);
        if (count == null) {
            return;
        }

        double[][] scores = new double[count][count];
        double[][] costs = new double[count][count];
        System.out.println("Enter compatibility scores from 0.0 to 1.0.");
        for (int candidate = 0; candidate < count; candidate++) {
            for (int job = 0; job < count; job++) {
                scores[candidate][job] = readCompatibilityScore(candidate + 1, job + 1);
                costs[candidate][job] = 1.0 - scores[candidate][job];
            }
        }

        try {
            int[] assignment = HungarianAlgorithm.solve(costs);
            List<String> lines = new ArrayList<>();
            double total = 0.0;
            for (int candidate = 0; candidate < assignment.length; candidate++) {
                int job = assignment[candidate];
                double score = scores[candidate][job];
                total += score;
                lines.add("Candidate %d -> Job %d   (score %.2f)"
                        .formatted(candidate + 1, job + 1, score));
            }
            lines.add("Total score: %.2f".formatted(total));
            printBox(lines.toArray(String[]::new));
        } catch (IllegalArgumentException exception) {
            System.out.println("Could not compute assignment: " + exception.getMessage());
        }
    }

    private static void demoCO5_Knapsack() {
        Integer count = readIntegerInRange("Number of items (1..20): ", 1, 20);
        if (count == null) {
            return;
        }

        int[] weights = new int[count];
        int[] values = new int[count];
        for (int item = 0; item < count; item++) {
            Integer weight = readIntegerInRange(
                    "Weight for item %d (1..1000000): ".formatted(item + 1), 1, 1_000_000);
            if (weight == null) {
                return;
            }
            Integer value = readIntegerInRange(
                    "Value for item %d (1..1000000): ".formatted(item + 1), 1, 1_000_000);
            if (value == null) {
                return;
            }
            weights[item] = weight;
            values[item] = value;
        }

        Integer capacity = readIntegerInRange("Capacity (0..1000000): ", 0, 1_000_000);
        if (capacity == null) {
            return;
        }

        try {
            int maximumValue = KnapsackDP.solve(weights, values, capacity);
            printBox(
                    "Items    : " + count,
                    "Capacity : " + capacity,
                    "Max Value: " + maximumValue);
        } catch (IllegalArgumentException exception) {
            System.out.println("Could not solve knapsack: " + exception.getMessage());
        }
    }

    private static void demoCO6_Primality() {
        Long number = readPositiveLong("Enter a positive integer n: ");
        if (number == null) {
            return;
        }

        System.out.print("Enter witnesses k (default 5): ");
        String witnessInput = readLine("");
        if (witnessInput == null) {
            return;
        }
        int witnesses = 5;
        try {
            int parsed = Integer.parseInt(witnessInput.trim());
            if (parsed > 0) {
                witnesses = parsed;
            } else {
                System.out.println("Invalid witness count; using the default k = 5.");
            }
        } catch (NumberFormatException exception) {
            System.out.println("Invalid witness count; using the default k = 5.");
        }
        witnesses = Math.min(witnesses, 10_000);

        long start = System.nanoTime();
        boolean prime = MillerRabin.isPrime(number, witnesses);
        long elapsed = System.nanoTime() - start;
        double confidence = (1.0 - Math.pow(0.25, witnesses)) * 100.0;
        printBox(
                "Number        : " + NUMBER_FORMAT.format(number),
                "Witnesses (k) : " + witnesses,
                "Verdict       : " + (prime ? "PRIME" : "NOT PRIME"),
                "Time          : " + elapsed + " ns",
                "Confidence    : %.6f%%".formatted(confidence));
    }

    private static void demoFullMatching() {
        Integer candidateCount = readIntegerInRange("Number of candidates (1..10): ", 1, 10);
        if (candidateCount == null) {
            return;
        }

        List<Candidate> candidates = new ArrayList<>();
        for (int index = 0; index < candidateCount; index++) {
            String name = readNonEmptyLine("Candidate %d name: ".formatted(index + 1));
            if (name == null) {
                return;
            }
            String resume = readNonEmptyLine(
                    "Candidate %d resume/skills: ".formatted(index + 1));
            if (resume == null) {
                return;
            }
            Candidate candidate = new Candidate("C" + (index + 1), name, resume);
            addExtractedTerms(candidate, resume);
            candidates.add(candidate);
        }

        Integer jobCount = readIntegerInRange("Number of jobs (1..10): ", 1, 10);
        if (jobCount == null) {
            return;
        }

        List<Job> jobs = new ArrayList<>();
        for (int index = 0; index < jobCount; index++) {
            String title = readNonEmptyLine("Job %d title: ".formatted(index + 1));
            if (title == null) {
                return;
            }
            String requirements = readNonEmptyLine(
                    "Job %d required skills/description: ".formatted(index + 1));
            if (requirements == null) {
                return;
            }
            Job job = new Job("J" + (index + 1), title, requirements);
            addExtractedTerms(job, requirements);
            jobs.add(job);
        }

        MatchingEngine engine = new MatchingEngine(candidates, jobs);
        List<MatchResult> rankedResults = engine.rankAll();
        System.out.printf("%-24s %-32s %s%n", "CANDIDATE", "JOB", "SCORE");
        System.out.println("────────────────────────────────────────────────────────────────────────────");
        for (MatchResult result : rankedResults) {
            System.out.printf(Locale.ROOT, "%-24s %-32s %.4f%n",
                    result.getCandidate().getName(),
                    result.getJob().getTitle(),
                    result.getScore());
        }

        Map<Candidate, MatchResult> bestByCandidate = new LinkedHashMap<>();
        for (MatchResult result : rankedResults) {
            bestByCandidate.putIfAbsent(result.getCandidate(), result);
        }
        System.out.println();
        System.out.println("Best match per candidate:");
        for (Candidate candidate : candidates) {
            MatchResult best = bestByCandidate.get(candidate);
            if (best != null) {
                System.out.printf(Locale.ROOT, "%s -> %s (%.2f%%)%n",
                        candidate.getName(), best.getJob().getTitle(), best.getScore() * 100.0);
            }
        }
    }

    private static void addExtractedTerms(Candidate candidate, String text) {
        Set<String> skills = TextProcessor.extractSkills(text);
        Set<String> keywords = TextProcessor.extractKeywords(text);
        skills.forEach(candidate::addSkill);
        keywords.forEach(candidate::addKeyword);
    }

    private static void addExtractedTerms(Job job, String text) {
        Set<String> skills = TextProcessor.extractSkills(text);
        Set<String> keywords = TextProcessor.extractKeywords(text);
        skills.forEach(job::addRequiredSkill);
        keywords.forEach(job::addKeyword);
    }

    private static double readCompatibilityScore(int candidate, int job) {
        while (true) {
            String input = readLine("Score for Candidate %d / Job %d: "
                    .formatted(candidate, job));
            if (input == null) {
                return 0.0;
            }
            final double score;
            try {
                score = Double.parseDouble(input.trim());
            } catch (NumberFormatException exception) {
                System.out.println("Invalid number; defaulting this cell to 0.0.");
                return 0.0;
            }
            if (Double.isFinite(score) && score >= 0.0 && score <= 1.0) {
                return score;
            }
            System.out.println("Enter a finite score from 0.0 to 1.0.");
        }
    }

    private static Integer readIntegerInRange(String prompt, int minimum, int maximum) {
        while (true) {
            String input = readLine(prompt);
            if (input == null) {
                return null;
            }
            try {
                int value = Integer.parseInt(input.trim());
                if (value >= minimum && value <= maximum) {
                    return value;
                }
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a whole number.");
                continue;
            }
            System.out.printf("Enter a value from %d to %d.%n", minimum, maximum);
        }
    }

    private static Long readPositiveLong(String prompt) {
        while (true) {
            String input = readLine(prompt);
            if (input == null) {
                return null;
            }
            try {
                long value = Long.parseLong(input.trim());
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid positive whole number.");
                continue;
            }
            System.out.println("The number must be greater than zero.");
        }
    }

    private static String readNonEmptyLine(String prompt) {
        while (true) {
            String input = readLine(prompt);
            if (input == null) {
                return null;
            }
            if (!input.isBlank()) {
                return input.trim();
            }
            System.out.println("Input cannot be empty.");
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!INPUT.hasNextLine()) {
            return null;
        }
        return INPUT.nextLine();
    }

    private static String firstMatch(List<Integer> matches) {
        return matches.isEmpty() ? "not found" : Integer.toString(matches.get(0));
    }

    private static void printBox(String... lines) {
        int width = 0;
        for (String line : lines) {
            width = Math.max(width, line.length());
        }
        String horizontal = "─".repeat(width + 2);
        System.out.println("┌" + horizontal + "┐");
        for (String line : lines) {
            System.out.printf("│ %-"
                    + width + "s │%n", line);
        }
        System.out.println("└" + horizontal + "┘");
    }

    private static void pause() {
        if (readLine("--- Press ENTER to continue ---") == null) {
            return;
        }
        System.out.println();
    }
}
