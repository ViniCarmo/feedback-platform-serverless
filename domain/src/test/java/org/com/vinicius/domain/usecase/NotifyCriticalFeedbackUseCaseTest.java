package org.com.vinicius.domain.usecase;

import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.gateway.CriticalFeedbackNotifier;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotifyCriticalFeedbackUseCaseTest {


    private static final Instant NOW = Instant.parse("2026-11-16T10:00:00Z");

    private final FakeNotifier notifier = new FakeNotifier();
    private final NotifyCriticalFeedbackUseCase useCase = new NotifyCriticalFeedbackUseCase(notifier);

    @Test
    void shouldNotifyWhenFeedbackIsCritical() {
        Feedback feedback = Feedback.create("Audio kept cutting out", 2, NOW);

        boolean notified = useCase.execute(feedback);

        assertTrue(notified);
        assertEquals(List.of(feedback), notifier.notified);
    }

    @Test
    void shouldIgnoreFeedbackThatIsNotCritical() {
        Feedback feedback = Feedback.create("Great class", 9, NOW);

        boolean notified = useCase.execute(feedback);

        assertFalse(notified);
        assertTrue(notifier.notified.isEmpty());
    }

    private static class FakeNotifier implements CriticalFeedbackNotifier {
        private final List<Feedback> notified = new ArrayList<>();

        @Override
        public void notifyCritical(Feedback feedback) {
            notified.add(feedback);
        }
    }

}