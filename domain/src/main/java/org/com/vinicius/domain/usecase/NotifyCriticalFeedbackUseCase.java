package org.com.vinicius.domain.usecase;

import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.gateway.CriticalFeedbackNotifier;

public class NotifyCriticalFeedbackUseCase {

    private final CriticalFeedbackNotifier notifier;

    public NotifyCriticalFeedbackUseCase(CriticalFeedbackNotifier notifier) {
        this.notifier = notifier;
    }

    public boolean execute(Feedback feedback) {
        if (!feedback.requiresImmediateNotification()) {
            return false;
        }
        notifier.notifyCritical(feedback);
        return true;
    }
}
