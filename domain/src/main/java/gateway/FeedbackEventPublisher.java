package gateway;

import entity.Feedback;

public interface FeedbackEventPublisher {
    void publishCritical(Feedback feedback);
}
