package domain.entity;

import domain.exception.InvalidFeedbackException;

import java.time.Instant;
import java.util.UUID;

public record Feedback(UUID id,
                       String description,
                       int score,
                       Urgency urgency,
                       Instant submittedAt) {

    public static final int MAX_DESCRIPTION_LENGTH = 1000;

    public Feedback {
        if (id == null) {
            throw new InvalidFeedbackException("Id is required");
        }
        if (description == null || description.isBlank()) {
            throw new InvalidFeedbackException("Description is required");
        }
        description = description.strip();
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new InvalidFeedbackException(
                    "Description must have at most " + MAX_DESCRIPTION_LENGTH + " characters");
        }
        if (urgency != Urgency.fromScore(score)) {
            throw new InvalidFeedbackException("Urgency does not match the score");
        }
        if (submittedAt == null) {
            throw new InvalidFeedbackException("Submission date is required");
        }
    }

    public static Feedback create(String description, int score, Instant submittedAt) {
        return new Feedback(UUID.randomUUID(), description, score, Urgency.fromScore(score), submittedAt);
    }

    public boolean requiresImmediateNotification() {
        return urgency.requiresImmediateNotification();
    }

}
