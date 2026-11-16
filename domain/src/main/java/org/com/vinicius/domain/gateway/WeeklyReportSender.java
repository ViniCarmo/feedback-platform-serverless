package org.com.vinicius.domain.gateway;

import org.com.vinicius.domain.entity.WeeklyReport;

public interface WeeklyReportSender {

    void send(WeeklyReport report, String reportLocation);
}
