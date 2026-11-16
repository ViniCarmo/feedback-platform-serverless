package org.com.vinicius.domain.gateway;

import org.com.vinicius.domain.entity.Feedback;

public interface FeedbackRepository {

    void save(Feedback feedback);
}
