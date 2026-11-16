package domain.entity;

import domain.exception.InvalidFeedbackException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FeedbackTest {
    private static final Instant NOW = Instant.parse("2026-11-12T10:00:00Z");

    @Test
    void shouldCreateFeedbackWithDerivedUrgency() {
        Feedback feedback = Feedback.create("Audio kept cutting out", 2, NOW);

        assertNotNull(feedback.id());
        assertEquals("Audio kept cutting out", feedback.description());
        assertEquals(2, feedback.score());
        assertEquals(Urgency.CRITICAL, feedback.urgency());
        assertEquals(NOW, feedback.submittedAt());
        assertTrue(feedback.requiresImmediateNotification());
    }

    @Test
    void shouldGenerateDifferentIdsForEachFeedback() {
        Feedback first = Feedback.create("Great class", 9, NOW);
        Feedback second = Feedback.create("Great class", 9, NOW);

        assertNotEquals(first.id(), second.id());
    }

    @Test
    void shouldStripDescription() {
        Feedback feedback = Feedback.create("   Great class   ", 9, NOW);

        assertEquals("Great class", feedback.description());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    void shouldRejectBlankDescription(String description) {
        assertThrows(InvalidFeedbackException.class, () -> Feedback.create(description, 5, NOW));
    }

    @Test
    void shouldRejectDescriptionLongerThanLimit() {
        String tooLong = "a".repeat(Feedback.MAX_DESCRIPTION_LENGTH + 1);

        assertThrows(InvalidFeedbackException.class, () -> Feedback.create(tooLong, 5, NOW));
    }

    @Test
    void shouldAcceptDescriptionAtTheLimit() {
        String atLimit = "a".repeat(Feedback.MAX_DESCRIPTION_LENGTH);

        assertDoesNotThrow(() -> Feedback.create(atLimit, 5, NOW));
    }

    @Test
    void shouldRejectInvalidScore() {
        assertThrows(InvalidFeedbackException.class, () -> Feedback.create("Any", 11, NOW));
    }

    @Test
    void shouldRejectMissingSubmissionDate() {
        assertThrows(InvalidFeedbackException.class, () -> Feedback.create("Any", 5, null));
    }

    @Test
    void shouldRejectUrgencyThatDoesNotMatchScore() {
        assertThrows(InvalidFeedbackException.class,
                () -> new Feedback(UUID.randomUUID(), "Any", 9, Urgency.CRITICAL, NOW));
    }


}
