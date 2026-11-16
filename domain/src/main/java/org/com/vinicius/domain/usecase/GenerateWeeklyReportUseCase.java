package org.com.vinicius.domain.usecase;

import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.entity.WeeklyReport;
import org.com.vinicius.domain.gateway.FeedbackQueryRepository;
import org.com.vinicius.domain.gateway.WeeklyReportSender;
import org.com.vinicius.domain.gateway.WeeklyReportStorage;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class GenerateWeeklyReportUseCase {

    private final FeedbackQueryRepository repository;
    private final WeeklyReportStorage storage;
    private final WeeklyReportSender sender;
    private final Clock clock;
    private final ZoneId zone;

    public GenerateWeeklyReportUseCase(FeedbackQueryRepository repository,
                                       WeeklyReportStorage storage,
                                       WeeklyReportSender sender,
                                       Clock clock,
                                       ZoneId zone) {
        this.repository = repository;
        this.storage = storage;
        this.sender = sender;
        this.clock = clock;
        this.zone = zone;
    }

    public WeeklyReport execute() {
        return execute(LocalDate.now(clock.withZone(zone)));
    }

    public WeeklyReport execute(LocalDate referenceDate) {
        LocalDate periodEnd = referenceDate.minusDays(1);
        LocalDate periodStart = periodEnd.minusDays(6);

        List<Feedback> feedbacks = repository.findBySubmittedDateBetween(periodStart, periodEnd);
        WeeklyReport report = WeeklyReport.generate(periodStart, periodEnd, feedbacks, zone);

        String location = storage.store(report);
        sender.send(report, location);
        return report;
    }
}
