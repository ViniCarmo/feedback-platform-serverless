package org.com.vinicius.receivefeedback.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.entity.Urgency;

import java.time.Instant;
import java.util.UUID;

@RegisterForReflection
public record FeedbackResponse(UUID id, Urgency urgency, Instant submittedAt) {

    public static FeedbackResponse from(Feedback feedback) {
        return new FeedbackResponse(feedback.id(), feedback.urgency(), feedback.submittedAt());
    }
}
