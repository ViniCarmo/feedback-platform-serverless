package org.com.vinicius.domain.gateway;

import org.com.vinicius.domain.entity.Feedback;

public interface CriticalFeedbackNotifier {

    void notifyCritical(Feedback feedback);
}
