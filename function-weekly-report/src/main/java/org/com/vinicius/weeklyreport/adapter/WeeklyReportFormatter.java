package org.com.vinicius.weeklyreport.adapter;

import jakarta.enterprise.context.ApplicationScoped;
import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.entity.WeeklyReport;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@ApplicationScoped
public class WeeklyReportFormatter {


    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("America/Sao_Paulo"));

    public String subject(WeeklyReport report) {
        return "Weekly feedback report - %s to %s"
                .formatted(DAY.format(report.periodStart()), DAY.format(report.periodEnd()));
    }

    public String format(WeeklyReport report) {
        StringBuilder text = new StringBuilder();

        text.append("WEEKLY FEEDBACK REPORT\n\n");
        text.append("Period: %s to %s\n".formatted(DAY.format(report.periodStart()), DAY.format(report.periodEnd())));
        text.append("Total feedbacks: %d\n".formatted(report.totalFeedbacks()));
        text.append(String.format(Locale.ROOT, "Average score: %.2f\n\n", report.averageScore()));

        text.append("Feedbacks per day:\n");
        report.countByDay().forEach((day, count) ->
                text.append("  %s: %d\n".formatted(DAY.format(day), count)));

        text.append("\nFeedbacks per urgency:\n");
        report.countByUrgency().forEach((urgency, count) ->
                text.append("  %s: %d\n".formatted(urgency, count)));

        text.append("\nFeedbacks (Brasilia time):\n");
        if (report.feedbacks().isEmpty()) {
            text.append("  No feedbacks in this period.\n");
        }
        for (Feedback feedback : report.feedbacks()) {
            text.append("  [%s] %s - score %d - %s\n".formatted(
                    DATE_TIME.format(feedback.submittedAt()),
                    feedback.urgency(),
                    feedback.score(),
                    feedback.description()));
        }

        return text.toString();
    }
}
