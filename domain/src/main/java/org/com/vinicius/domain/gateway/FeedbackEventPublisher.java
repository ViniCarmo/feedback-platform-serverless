package org.com.vinicius.domain.gateway;

import org.com.vinicius.domain.entity.Feedback;

public interface FeedbackEventPublisher {
    void publishCritical(Feedback feedback);
}
