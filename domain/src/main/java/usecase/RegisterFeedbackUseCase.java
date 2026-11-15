package usecase;

import entity.Feedback;
import gateway.FeedbackEventPublisher;
import gateway.FeedbackRepository;

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
