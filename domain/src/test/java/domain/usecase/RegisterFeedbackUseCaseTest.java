package domain.usecase;

import domain.entity.Feedback;
import domain.entity.Urgency;
import domain.exception.InvalidFeedbackException;
import domain.gateway.FeedbackEventPublisher;
import domain.gateway.FeedbackRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegisterFeedbackUseCaseTest {

    private static final Instant NOW = Instant.parse("2026-11-12T10:00:00Z");

    private final FakeFeedbackRepository repository = new FakeFeedbackRepository();
    private final FakeFeedbackEventPublisher eventPublisher = new FakeFeedbackEventPublisher();
    private final RegisterFeedbackUseCase useCase =
            new RegisterFeedbackUseCase(repository, eventPublisher, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void shouldSaveFeedback() {
        Feedback feedback = useCase.execute("Great class", 9);

        assertEquals(List.of(feedback), repository.saved);
    }

    @Test
    void shouldUseClockTimeAsSubmissionDate() {
        Feedback feedback = useCase.execute("Great class", 9);

        assertEquals(NOW, feedback.submittedAt());
    }

    @Test
    void shouldPublishEventWhenFeedbackIsCritical() {
        Feedback feedback = useCase.execute("Audio kept cutting out", 2);

        assertEquals(Urgency.CRITICAL, feedback.urgency());
        assertEquals(List.of(feedback), eventPublisher.published);
    }

    @Test
    void shouldNotPublishEventWhenFeedbackIsNotCritical() {
        useCase.execute("A bit fast", 5);

        assertTrue(eventPublisher.published.isEmpty());
    }

    @Test
    void shouldNotSaveNorPublishWhenFeedbackIsInvalid() {
        assertThrows(InvalidFeedbackException.class, () -> useCase.execute("", 2));

        assertTrue(repository.saved.isEmpty());
        assertTrue(eventPublisher.published.isEmpty());
    }

    private static class FakeFeedbackRepository implements FeedbackRepository {
        private final List<Feedback> saved = new ArrayList<>();

        @Override
        public void save(Feedback feedback) {
            saved.add(feedback);
        }
    }

    private static class FakeFeedbackEventPublisher implements FeedbackEventPublisher {
        private final List<Feedback> published = new ArrayList<>();

        @Override
        public void publishCritical(Feedback feedback) {
            published.add(feedback);
        }
    }

}