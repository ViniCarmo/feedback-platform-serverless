package entity;

import exception.InvalidFeedbackException;

public enum Urgency {
    CRITICAL,
    MEDIUM,
    LOW;

    public static Urgency fromScore(int score) {
        if (score < 0 || score > 10) {
            throw new InvalidFeedbackException("Score must be between 0 and 10");
        }
        if (score <= 3) {
            return CRITICAL;
        }
        if (score <= 6) {
            return MEDIUM;
        }
        return LOW;
    }

    public boolean requiresImmediateNotification() {
        return this == CRITICAL;
    }

    }

