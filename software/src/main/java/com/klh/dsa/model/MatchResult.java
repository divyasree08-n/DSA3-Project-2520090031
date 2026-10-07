package com.klh.dsa.model;

/** A scored candidate/job pair, ordered from highest score to lowest. */
public final class MatchResult implements Comparable<MatchResult> {
    private final Candidate candidate;
    private final Job job;
    private final double score;

    /** Creates a result with score clamped to the valid [0, 1] range. */
    public MatchResult(Candidate candidate, Job job, double score) {
        this.candidate = candidate;
        this.job = job;
        this.score = Double.isFinite(score) ? Math.max(0.0, Math.min(1.0, score)) : 0.0;
    }

    /** Returns the candidate, or null if none was supplied. */
    public Candidate getCandidate() {
        return candidate;
    }

    /** Returns the job, or null if none was supplied. */
    public Job getJob() {
        return job;
    }

    /** Returns the normalized match score. */
    public double getScore() {
        return score;
    }

    @Override
    public int compareTo(MatchResult other) {
        int byScore = Double.compare(other.score, score);
        if (byScore != 0) {
            return byScore;
        }
        String candidateId = candidate == null ? "" : candidate.getId();
        String otherCandidateId = other.candidate == null ? "" : other.candidate.getId();
        int byCandidate = candidateId.compareTo(otherCandidateId);
        if (byCandidate != 0) {
            return byCandidate;
        }
        String jobId = job == null ? "" : job.getId();
        String otherJobId = other.job == null ? "" : other.job.getId();
        return jobId.compareTo(otherJobId);
    }

    @Override
    public String toString() {
        String candidateName = candidate == null ? "Unknown candidate" : candidate.getName();
        String jobTitle = job == null ? "Unknown job" : job.getTitle();
        return "%s -> %s (%.1f%%)".formatted(candidateName, jobTitle, score * 100.0);
    }
}
