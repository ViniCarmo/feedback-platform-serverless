package org.com.vinicius.domain.usecase;

import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.entity.WeeklyReport;
import org.com.vinicius.domain.gateway.FeedbackQueryRepository;
import org.com.vinicius.domain.gateway.WeeklyReportSender;
import org.com.vinicius.domain.gateway.WeeklyReportStorage;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenerateWeeklyReportUseCaseTest {


    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");
    // Monday, Nov 16 2026, 08:00 in Sao Paulo
    private static final Instant NOW = Instant.parse("2026-11-16T11:00:00Z");

    private final FakeQueryRepository repository = new FakeQueryRepository();
    private final FakeStorage storage = new FakeStorage();
    private final FakeSender sender = new FakeSender();
    private final GenerateWeeklyReportUseCase useCase = new GenerateWeeklyReportUseCase(
            repository, storage, sender, Clock.fixed(NOW, ZoneOffset.UTC), SAO_PAULO);

    @Test
    void shouldGenerateReportForThePreviousSevenDays() {
        WeeklyReport report = useCase.execute();

        assertEquals(LocalDate.of(2026, 11, 9), repository.requestedStart);
        assertEquals(LocalDate.of(2026, 11, 15), repository.requestedEnd);
        assertEquals(LocalDate.of(2026, 11, 9), report.periodStart());
        assertEquals(LocalDate.of(2026, 11, 15), report.periodEnd());
    }

    @Test
    void shouldBuildReportFromRepositoryFeedbacks() {
        repository.feedbacks.add(Feedback.create("Great class", 9, Instant.parse("2026-11-10T13:00:00Z")));
        repository.feedbacks.add(Feedback.create("Audio issues", 2, Instant.parse("2026-11-11T13:00:00Z")));

        WeeklyReport report = useCase.execute();

        assertEquals(2, report.totalFeedbacks());
        assertEquals(5.5, report.averageScore(), 0.0001);
    }

    @Test
    void shouldStoreAndSendTheReport() {
        WeeklyReport report = useCase.execute();

        assertEquals(List.of(report), storage.stored);
        assertEquals(report, sender.sentReport);
        assertEquals("reports/fake-location", sender.sentLocation);
    }

    private static class FakeQueryRepository implements FeedbackQueryRepository {
        private final List<Feedback> feedbacks = new ArrayList<>();
        private LocalDate requestedStart;
        private LocalDate requestedEnd;

        @Override
        public List<Feedback> findBySubmittedDateBetween(LocalDate startInclusive, LocalDate endInclusive) {
            requestedStart = startInclusive;
            requestedEnd = endInclusive;
            return feedbacks;
        }
    }

    private static class FakeStorage implements WeeklyReportStorage {
        private final List<WeeklyReport> stored = new ArrayList<>();

        @Override
        public String store(WeeklyReport report) {
            stored.add(report);
            return "reports/fake-location";
        }
    }

    private static class FakeSender implements WeeklyReportSender {
        private WeeklyReport sentReport;
        private String sentLocation;

        @Override
        public void send(WeeklyReport report, String reportLocation) {
            sentReport = report;
            sentLocation = reportLocation;
        }
    }

}