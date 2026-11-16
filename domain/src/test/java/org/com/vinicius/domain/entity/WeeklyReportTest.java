package org.com.vinicius.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeeklyReportTest {

    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");
    private static final LocalDate START = LocalDate.of(2026, 11, 9);
    private static final LocalDate END = LocalDate.of(2026, 11, 15);

    private static Feedback feedback(int score, String submittedAt) {
        return Feedback.create("Any description", score, Instant.parse(submittedAt));
    }

    @Test
    void shouldCalculateAverageScore() {
        List<Feedback> feedbacks = List.of(
                feedback(2, "2026-11-10T13:00:00Z"),
                feedback(5, "2026-11-11T13:00:00Z"),
                feedback(9, "2026-11-12T13:00:00Z"));

        WeeklyReport report = WeeklyReport.generate(START, END, feedbacks, SAO_PAULO);

        assertEquals(16.0 / 3, report.averageScore(), 0.0001);
        assertEquals(3, report.totalFeedbacks());
    }

    @Test
    void shouldCountFeedbacksPerDayIncludingDaysWithoutFeedback() {
        List<Feedback> feedbacks = List.of(
                feedback(8, "2026-11-10T13:00:00Z"),
                feedback(7, "2026-11-10T15:00:00Z"),
                feedback(9, "2026-11-12T13:00:00Z"));

        WeeklyReport report = WeeklyReport.generate(START, END, feedbacks, SAO_PAULO);

        assertEquals(7, report.countByDay().size());
        assertEquals(0L, report.countByDay().get(LocalDate.of(2026, 11, 9)));
        assertEquals(2L, report.countByDay().get(LocalDate.of(2026, 11, 10)));
        assertEquals(1L, report.countByDay().get(LocalDate.of(2026, 11, 12)));
    }

    @Test
    void shouldCountFeedbacksPerUrgencyIncludingUrgenciesWithoutFeedback() {
        List<Feedback> feedbacks = List.of(
                feedback(1, "2026-11-10T13:00:00Z"),
                feedback(2, "2026-11-11T13:00:00Z"),
                feedback(8, "2026-11-12T13:00:00Z"));

        WeeklyReport report = WeeklyReport.generate(START, END, feedbacks, SAO_PAULO);

        assertEquals(2L, report.countByUrgency().get(Urgency.CRITICAL));
        assertEquals(0L, report.countByUrgency().get(Urgency.MEDIUM));
        assertEquals(1L, report.countByUrgency().get(Urgency.LOW));
    }

    @Test
    void shouldUseBusinessZoneToDecideTheDay() {
        // 02:00 UTC on Nov 10 is 23:00 on Nov 9 in Sao Paulo
        List<Feedback> feedbacks = List.of(feedback(8, "2026-11-10T02:00:00Z"));

        WeeklyReport report = WeeklyReport.generate(START, END, feedbacks, SAO_PAULO);

        assertEquals(1L, report.countByDay().get(LocalDate.of(2026, 11, 9)));
        assertEquals(0L, report.countByDay().get(LocalDate.of(2026, 11, 10)));
    }

    @Test
    void shouldIgnoreFeedbacksOutsideThePeriod() {
        List<Feedback> feedbacks = List.of(
                feedback(8, "2026-11-12T13:00:00Z"),
                feedback(2, "2026-11-16T13:00:00Z"));

        WeeklyReport report = WeeklyReport.generate(START, END, feedbacks, SAO_PAULO);

        assertEquals(1, report.totalFeedbacks());
        assertEquals(8.0, report.averageScore(), 0.0001);
    }

    @Test
    void shouldHandleWeekWithoutFeedbacks() {
        WeeklyReport report = WeeklyReport.generate(START, END, List.of(), SAO_PAULO);

        assertEquals(0, report.totalFeedbacks());
        assertEquals(0.0, report.averageScore());
        assertTrue(report.countByDay().values().stream().allMatch(count -> count == 0L));
        assertTrue(report.countByUrgency().values().stream().allMatch(count -> count == 0L));
    }

    @Test
    void shouldListFeedbacksOrderedBySubmissionDate() {
        Feedback later = feedback(8, "2026-11-12T13:00:00Z");
        Feedback earlier = feedback(5, "2026-11-10T13:00:00Z");

        WeeklyReport report = WeeklyReport.generate(START, END, List.of(later, earlier), SAO_PAULO);

        assertEquals(List.of(earlier, later), report.feedbacks());
    }
}