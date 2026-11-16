package org.com.vinicius.domain.entity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public record WeeklyReport (LocalDate periodStart,
                            LocalDate periodEnd,
                            List<Feedback> feedbacks,
                            double averageScore,
                            Map<LocalDate, Long> countByDay,
                            Map<Urgency, Long> countByUrgency){

    public static WeeklyReport generate(LocalDate periodStart,
                                        LocalDate periodEnd,
                                        List<Feedback> feedbacks,
                                        ZoneId zone) {
        if (periodEnd.isBefore(periodStart)) {
            throw new IllegalArgumentException("Period end must not be before period start");
        }

        List<Feedback> inPeriod = feedbacks.stream()
                .filter(feedback -> {
                    LocalDate day = LocalDate.ofInstant(feedback.submittedAt(), zone);
                    return !day.isBefore(periodStart) && !day.isAfter(periodEnd);
                })
                .sorted(Comparator.comparing(Feedback::submittedAt))
                .toList();

        Map<LocalDate, Long> countByDay = new TreeMap<>();
        for (LocalDate day = periodStart; !day.isAfter(periodEnd); day = day.plusDays(1)) {
            countByDay.put(day, 0L);
        }

        Map<Urgency, Long> countByUrgency = new EnumMap<>(Urgency.class);
        for (Urgency urgency : Urgency.values()) {
            countByUrgency.put(urgency, 0L);
        }

        for (Feedback feedback : inPeriod) {
            countByDay.merge(LocalDate.ofInstant(feedback.submittedAt(), zone), 1L, Long::sum);
            countByUrgency.merge(feedback.urgency(), 1L, Long::sum);
        }

        double averageScore = inPeriod.stream()
                .mapToInt(Feedback::score)
                .average()
                .orElse(0.0);

        return new WeeklyReport(
                periodStart,
                periodEnd,
                inPeriod,
                averageScore,
                Collections.unmodifiableMap(countByDay),
                Collections.unmodifiableMap(countByUrgency));
    }

    public int totalFeedbacks() {
        return feedbacks.size();
    }
}
