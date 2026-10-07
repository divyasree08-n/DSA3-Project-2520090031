package com.klh.dsa.nlp;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/** Tokenization and lightweight keyword/skill extraction for resumes and jobs. */
public final class TextProcessor {
    /** Common words omitted from extracted keyword sets. */
    public static final Set<String> STOPWORDS = Collections.unmodifiableSet(new LinkedHashSet<>(
            Arrays.asList("a", "an", "and", "are", "as", "at", "be", "been", "by", "for",
                    "from", "has", "have", "in", "is", "it", "of", "on", "or", "our", "the",
                    "to", "was", "were", "with", "you", "your")));
    /** Recognized technology skills used by the demo application. */
    public static final Set<String> SKILL_DICTIONARY = Collections.unmodifiableSet(new LinkedHashSet<>(
            Arrays.asList("java", "python", "sql", "spring", "javascript", "typescript", "react",
                    "node", "nodejs", "c", "c++", "c#", "go", "rust", "docker", "kubernetes",
                    "aws", "azure", "html", "css", "machine", "learning", "tensorflow", "pytorch",
                    "mongodb", "postgresql", "mysql", "git", "linux", "algorithms", "data",
                    "structures", "communication", "leadership")));

    private TextProcessor() {
    }

    /** Splits text into lowercase alphanumeric tokens. */
    public static List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}+#.]+"))
                .filter(token -> !token.isBlank())
                .collect(Collectors.toList());
    }

    /** Extracts non-stopword tokens, retaining first-seen order. */
    public static Set<String> extractKeywords(String text) {
        Set<String> keywords = new LinkedHashSet<>();
        for (String token : tokenize(text)) {
            if (!STOPWORDS.contains(token) && token.length() > 1) {
                keywords.add(token);
            }
        }
        return keywords;
    }

    /** Extracts tokens present in the recognized skill dictionary. */
    public static Set<String> extractSkills(String text) {
        Set<String> skills = new LinkedHashSet<>();
        for (String token : tokenize(text)) {
            if (SKILL_DICTIONARY.contains(token)) {
                skills.add(token);
            }
        }
        return skills;
    }
}
