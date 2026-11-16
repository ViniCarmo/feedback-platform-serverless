package org.com.vinicius.notifycritical.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.com.vinicius.domain.gateway.CriticalFeedbackNotifier;
import org.com.vinicius.domain.usecase.NotifyCriticalFeedbackUseCase;

@ApplicationScoped
public class UseCaseConfig {

    @Produces
    @Singleton
    public NotifyCriticalFeedbackUseCase notifyCriticalFeedbackUseCase(CriticalFeedbackNotifier notifier) {
        return new NotifyCriticalFeedbackUseCase(notifier);
    }
}
