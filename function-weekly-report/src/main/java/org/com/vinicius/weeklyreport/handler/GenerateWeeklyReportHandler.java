package org.com.vinicius.weeklyreport.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.com.vinicius.domain.entity.WeeklyReport;
import org.com.vinicius.domain.usecase.GenerateWeeklyReportUseCase;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Named("generateWeeklyReport")
public class GenerateWeeklyReportHandler implements RequestHandler<Map<String, Object>, String> {


    private static final Logger LOG = Logger.getLogger(GenerateWeeklyReportHandler.class);

    private final GenerateWeeklyReportUseCase generateWeeklyReport;

    @Inject
    public GenerateWeeklyReportHandler(GenerateWeeklyReportUseCase generateWeeklyReport) {
        this.generateWeeklyReport = generateWeeklyReport;
    }

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        WeeklyReport report = referenceDateFrom(event)
                .map(generateWeeklyReport::execute)
                .orElseGet(generateWeeklyReport::execute);

        String summary = String.format(Locale.ROOT,
                "Weekly report generated: period=%s..%s total=%d average=%.2f",
                report.periodStart(), report.periodEnd(), report.totalFeedbacks(), report.averageScore());
        LOG.info(summary);
        return summary;
    }

    private Optional<LocalDate> referenceDateFrom(Map<String, Object> event) {
        if (event == null || event.get("referenceDate") == null) {
            return Optional.empty();
        }
        return Optional.of(LocalDate.parse(event.get("referenceDate").toString()));
    }


}
