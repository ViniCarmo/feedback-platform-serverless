package org.com.vinicius.domain.gateway;

import org.com.vinicius.domain.entity.WeeklyReport;

public interface WeeklyReportStorage {

    String store(WeeklyReport report);
}
