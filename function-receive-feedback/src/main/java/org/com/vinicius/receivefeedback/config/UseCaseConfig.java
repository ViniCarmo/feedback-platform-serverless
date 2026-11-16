package org.com.vinicius.receivefeedback.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.com.vinicius.domain.gateway.FeedbackEventPublisher;
import org.com.vinicius.domain.gateway.FeedbackRepository;
import org.com.vinicius.domain.usecase.RegisterFeedbackUseCase;

import java.time.Clock;

@ApplicationScoped
public class UseCaseConfig {

    @Produces
    @Singleton
    public RegisterFeedbackUseCase registerFeedbackUseCase(FeedbackRepository repository,
                                                           FeedbackEventPublisher eventPublisher) {
        return new RegisterFeedbackUseCase(repository, eventPublisher, Clock.systemUTC());
    }
}
