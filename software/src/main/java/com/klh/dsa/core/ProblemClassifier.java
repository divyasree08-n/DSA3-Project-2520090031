package com.klh.dsa.core;

import java.util.Locale;

/** Classifies marketplace requests and maps them to suitable algorithms. */
public final class ProblemClassifier {
    /** Supported query types for the talent marketplace. */
    public enum QueryType {
        EXACT_KEYWORD_SEARCH,
        FUZZY_SKILL_MATCH,
        MULTI_PATTERN_SEARCH,
        DOCUMENT_SIMILARITY,
        CANDIDATE_JOB_ASSIGNMENT,
        INTERVIEW_SCHEDULING,
        FAST_PRIME_HASH
    }

    private ProblemClassifier() {
    }

    /** Classifies a free-form query using simple keyword cues. */
    public static QueryType classify(String query) {
        String normalized = query == null ? "" : query.toLowerCase(Locale.ROOT);
        if (containsAny(normalized, "prime", "hash", "probabilistic")) {
            return QueryType.FAST_PRIME_HASH;
        }
        if (containsAny(normalized, "schedule", "interview", "calendar")) {
            return QueryType.INTERVIEW_SCHEDULING;
        }
        if (containsAny(normalized, "assign", "allocation", "best candidates")) {
            return QueryType.CANDIDATE_JOB_ASSIGNMENT;
        }
        if (containsAny(normalized, "similarity", "compare resumes", "document")) {
            return QueryType.DOCUMENT_SIMILARITY;
        }
        if (containsAny(normalized, "multiple", "many patterns", "all keywords")) {
            return QueryType.MULTI_PATTERN_SEARCH;
        }
        if (containsAny(normalized, "fuzzy", "similar skill", "near match", "typo")) {
            return QueryType.FUZZY_SKILL_MATCH;
        }
        return QueryType.EXACT_KEYWORD_SEARCH;
    }

    /** Returns the primary algorithm associated with a query type. */
    public static String algorithmFor(QueryType type) {
        if (type == null) {
            return "KMP";
        }
        return switch (type) {
            case EXACT_KEYWORD_SEARCH -> "KMP";
            case FUZZY_SKILL_MATCH -> "Levenshtein Edit Distance";
            case MULTI_PATTERN_SEARCH -> "Aho-Corasick";
            case DOCUMENT_SIMILARITY -> "Suffix Array + LCP";
            case CANDIDATE_JOB_ASSIGNMENT -> "Hungarian Algorithm";
            case INTERVIEW_SCHEDULING -> "Earliest-Finish-Time Greedy";
            case FAST_PRIME_HASH -> "Miller-Rabin";
        };
    }

    private static boolean containsAny(String query, String... phrases) {
        for (String phrase : phrases) {
            if (query.contains(phrase)) {
                return true;
            }
        }
        return false;
    }
}
