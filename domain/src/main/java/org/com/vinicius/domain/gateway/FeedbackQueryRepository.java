package org.com.vinicius.domain.gateway;

import org.com.vinicius.domain.entity.Feedback;

import java.time.LocalDate;
import java.util.List;

public interface FeedbackQueryRepository {

    List<Feedback> findBySubmittedDateBetween(LocalDate startInclusive, LocalDate endInclusive);
}
