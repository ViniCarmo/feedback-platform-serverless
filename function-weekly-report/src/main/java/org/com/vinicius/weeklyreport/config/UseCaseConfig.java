package org.com.vinicius.weeklyreport.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.com.vinicius.domain.gateway.FeedbackQueryRepository;
import org.com.vinicius.domain.gateway.WeeklyReportSender;
import org.com.vinicius.domain.gateway.WeeklyReportStorage;
import org.com.vinicius.domain.usecase.GenerateWeeklyReportUseCase;

import java.time.Clock;
import java.time.ZoneId;

@ApplicationScoped
public class UseCaseConfig {

    @Produces
    @Singleton
    public GenerateWeeklyReportUseCase generateWeeklyReportUseCase(FeedbackQueryRepository repository,
                                                                   WeeklyReportStorage storage,
                                                                   WeeklyReportSender sender) {
        return new GenerateWeeklyReportUseCase(
                repository, storage, sender, Clock.systemUTC(), ZoneId.of("America/Sao_Paulo"));
    }
}
