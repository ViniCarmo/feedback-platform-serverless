package org.com.vinicius.receivefeedback.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.com.vinicius.domain.entity.Feedback;

import java.time.Instant;
import java.util.UUID;

@RegisterForReflection
public record CriticalFeedbackEvent(UUID id,
                                    String description,
                                    int score,
                                    String urgency,
                                    Instant submittedAt) {

    public static CriticalFeedbackEvent from(Feedback feedback) {
        return new CriticalFeedbackEvent(
                feedback.id(),
                feedback.description(),
                feedback.score(),
                feedback.urgency().name(),
                feedback.submittedAt()
        );
    }
}
