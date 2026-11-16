package org.com.vinicius.domain.usecase;

import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.gateway.FeedbackEventPublisher;
import org.com.vinicius.domain.gateway.FeedbackRepository;

import java.time.Clock;

public class RegisterFeedbackUseCase {

    private final FeedbackRepository repository;
    private final FeedbackEventPublisher eventPublisher;
    private final Clock clock;

    public RegisterFeedbackUseCase(FeedbackRepository repository, FeedbackEventPublisher eventPublisher, Clock clock) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    public Feedback execute(String description, int score){
        Feedback feedback = Feedback.create(description, score, clock.instant());

        repository.save(feedback);
        if (feedback.requiresImmediateNotification()) {
            eventPublisher.publishCritical(feedback);
        }
        return feedback;
    }
}
