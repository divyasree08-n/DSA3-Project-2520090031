package com.klh.dsa.matcher;

import com.klh.dsa.algorithms.EditDistance;
import com.klh.dsa.algorithms.Trie;
import com.klh.dsa.model.Candidate;
import com.klh.dsa.model.Job;
import com.klh.dsa.model.MatchResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** Scores and ranks candidate/job pairs using skills, trie lookups, and text similarity. */
public final class MatchingEngine {
    private final List<Candidate> candidates;
    private final List<Job> jobs;

    /** Creates an engine over snapshots of the supplied candidate and job lists. */
    public MatchingEngine(List<Candidate> candidates, List<Job> jobs) {
        this.candidates = candidates == null ? List.of() : new ArrayList<>(candidates);
        this.jobs = jobs == null ? List.of() : new ArrayList<>(jobs);
    }

    /** Computes a weighted match score in the inclusive range [0, 1]. */
    public double score(Candidate candidate, Job job) {
        if (candidate == null || job == null) {
            return 0.0;
        }
        double skills = jaccard(candidate.getSkills(), job.getRequiredSkills());
        double trieHits = trieHitScore(candidate, job);
        double textSimilarity = keywordSimilarity(candidate.getKeywords(), job.getKeywords());
        return Math.max(0.0, Math.min(1.0, skills * 0.50 + trieHits * 0.30 + textSimilarity * 0.20));
    }

    /** Returns every pair sorted by descending score. */
    public List<MatchResult> rankAll() {
        List<MatchResult> results = new ArrayList<>(candidates.size() * jobs.size());
        for (Candidate candidate : candidates) {
            for (Job job : jobs) {
                results.add(new MatchResult(candidate, job, score(candidate, job)));
            }
        }
        Collections.sort(results);
        return results;
    }

    /** Returns at most n highest-scoring jobs for a candidate. */
    public List<MatchResult> topJobsFor(Candidate candidate, int n) {
        if (n <= 0 || candidate == null) {
            return List.of();
        }
        List<MatchResult> results = new ArrayList<>(jobs.size());
        for (Job job : jobs) {
            results.add(new MatchResult(candidate, job, score(candidate, job)));
        }
        Collections.sort(results);
        return new ArrayList<>(results.subList(0, Math.min(n, results.size())));
    }

    /** Returns at most n highest-scoring candidates for a job. */
    public List<MatchResult> topCandidatesFor(Job job, int n) {
        if (n <= 0 || job == null) {
            return List.of();
        }
        List<MatchResult> results = new ArrayList<>(candidates.size());
        for (Candidate candidate : candidates) {
            results.add(new MatchResult(candidate, job, score(candidate, job)));
        }
        Collections.sort(results);
        return new ArrayList<>(results.subList(0, Math.min(n, results.size())));
    }

    private static double jaccard(Set<String> first, Set<String> second) {
        if (first.isEmpty() && second.isEmpty()) {
            return 1.0;
        }
        int intersection = 0;
        for (String value : first) {
            if (second.contains(value)) {
                intersection++;
            }
        }
        int union = first.size() + second.size() - intersection;
        return union == 0 ? 0.0 : (double) intersection / union;
    }

    private static double trieHitScore(Candidate candidate, Job job) {
        Set<String> jobTerms = new TreeSet<>(job.getRequiredSkills());
        jobTerms.addAll(job.getKeywords());
        Set<String> candidateTerms = new TreeSet<>(candidate.getSkills());
        candidateTerms.addAll(candidate.getKeywords());
        if (jobTerms.isEmpty()) {
            return candidateTerms.isEmpty() ? 1.0 : 0.0;
        }
        Trie trie = new Trie();
        for (String term : jobTerms) {
            trie.insert(term);
        }
        int hits = 0;
        for (String term : candidateTerms) {
            if (trie.search(term)) {
                hits++;
            }
        }
        return Math.min(1.0, (double) hits / jobTerms.size());
    }

    private static double keywordSimilarity(Set<String> first, Set<String> second) {
        if (first.isEmpty() && second.isEmpty()) {
            return 1.0;
        }
        if (first.isEmpty() || second.isEmpty()) {
            return 0.0;
        }
        String left = String.join(" ", new TreeSet<>(first));
        String right = String.join(" ", new TreeSet<>(second));
        return EditDistance.similarity(left, right);
    }
}
