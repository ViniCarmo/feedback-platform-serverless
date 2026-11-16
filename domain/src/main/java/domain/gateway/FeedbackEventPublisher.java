package domain.gateway;

import domain.entity.Feedback;

public interface FeedbackEventPublisher {
    void publishCritical(Feedback feedback);
}
