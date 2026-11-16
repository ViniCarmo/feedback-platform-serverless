package domain.gateway;

import domain.entity.Feedback;

public interface FeedbackRepository {

    void save(Feedback feedback);
}
