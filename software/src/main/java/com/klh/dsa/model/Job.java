package com.klh.dsa.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/** A marketplace job posting and its matching requirements. */
public final class Job {
    private final String id;
    private final String title;
    private final String description;
    private final Set<String> requiredSkills = new LinkedHashSet<>();
    private final Set<String> keywords = new LinkedHashSet<>();

    /** Creates a job with the supplied identifying information. */
    public Job(String id, String title, String description) {
        this.id = id == null ? "" : id;
        this.title = title == null ? "" : title;
        this.description = description == null ? "" : description;
    }

    /** Returns the job identifier. */
    public String getId() {
        return id;
    }

    /** Returns the job title. */
    public String getTitle() {
        return title;
    }

    /** Returns the job description. */
    public String getDescription() {
        return description;
    }

    /** Returns the required skills as an unmodifiable set. */
    public Set<String> getRequiredSkills() {
        return Collections.unmodifiableSet(requiredSkills);
    }

    /** Returns the job keywords as an unmodifiable set. */
    public Set<String> getKeywords() {
        return Collections.unmodifiableSet(keywords);
    }

    /** Adds a normalized required skill when it is non-empty. */
    public void addRequiredSkill(String skill) {
        addNormalized(requiredSkills, skill);
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
        return "Job{id='%s', title='%s'}".formatted(id, title);
    }
}
