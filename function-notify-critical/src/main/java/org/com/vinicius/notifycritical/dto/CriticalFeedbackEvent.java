package org.com.vinicius.notifycritical.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.entity.Urgency;

import java.time.Instant;
import java.util.UUID;

@RegisterForReflection
public record CriticalFeedbackEvent( UUID id,
                                     String description,
                                     int score,
                                     String urgency,
                                     Instant submittedAt

) {

    public Feedback toDomain() {
        return new Feedback(id, description, score, Urgency.valueOf(urgency), submittedAt);
    }

}
