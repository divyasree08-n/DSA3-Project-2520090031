package com.klh.dsa.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/** A job-seeking candidate and their resume-derived attributes. */
public final class Candidate {
    private final String id;
    private final String name;
    private final String resumeText;
    private final Set<String> skills = new LinkedHashSet<>();
    private final Set<String> keywords = new LinkedHashSet<>();

    /** Creates a candidate with the supplied identifying information. */
    public Candidate(String id, String name, String resumeText) {
        this.id = id == null ? "" : id;
        this.name = name == null ? "" : name;
        this.resumeText = resumeText == null ? "" : resumeText;
    }

    /** Returns the candidate identifier. */
    public String getId() {
        return id;
    }

    /** Returns the candidate name. */
    public String getName() {
        return name;
    }

    /** Returns the resume text. */
    public String getResumeText() {
        return resumeText;
    }

    /** Returns the candidate's skills as an unmodifiable set. */
    public Set<String> getSkills() {
        return Collections.unmodifiableSet(skills);
    }

    /** Returns the candidate's keywords as an unmodifiable set. */
    public Set<String> getKeywords() {
        return Collections.unmodifiableSet(keywords);
    }

    /** Adds a normalized skill when it is non-empty. */
    public void addSkill(String skill) {
        addNormalized(skills, skill);
    }

    /** Adds a normalized keyword when it is non-empty. */
    public void addKeyword(String keyword) {
        addNormalized(keywords, keyword);
    }

    private static void addNormalized(Set<String> destination, String value) {
        if (value != null && !value.isBlank()) {
            destination.add(value.trim().toLowerCase(java.util.Locale.ROOT));
        }
    }

    @Override
    public String toString() {
        return "Candidate{id='%s', name='%s'}".formatted(id, name);
    }
}
