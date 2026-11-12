package domain.entity;

import entity.Urgency;
import exception.InvalidFeedbackException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UrgencyTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void shouldBeCriticalForScoresFromZeroToThree(int score) {
        assertEquals(Urgency.CRITICAL, Urgency.fromScore(score));
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 5, 6})
    void shouldBeMediumForScoresFromFourToSix(int score) {
        assertEquals(Urgency.MEDIUM, Urgency.fromScore(score));
    }

    @ParameterizedTest
    @ValueSource(ints = {7, 8, 9, 10})
    void shouldBeLowForScoresFromSevenToTen(int score) {
        assertEquals(Urgency.LOW, Urgency.fromScore(score));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 11, 100})
    void shouldRejectScoresOutOfRange(int score) {
        assertThrows(InvalidFeedbackException.class, () -> Urgency.fromScore(score));
    }

    @Test
    void onlyCriticalRequiresImmediateNotification() {
        assertTrue(Urgency.CRITICAL.requiresImmediateNotification());
        assertFalse(Urgency.MEDIUM.requiresImmediateNotification());
        assertFalse(Urgency.LOW.requiresImmediateNotification());
    }
}
