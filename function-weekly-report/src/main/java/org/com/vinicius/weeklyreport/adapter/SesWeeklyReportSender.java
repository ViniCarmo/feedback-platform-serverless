package org.com.vinicius.weeklyreport.adapter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.com.vinicius.domain.entity.WeeklyReport;
import org.com.vinicius.domain.gateway.WeeklyReportSender;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.List;

@ApplicationScoped
public class SesWeeklyReportSender implements WeeklyReportSender {

    private final SesClient ses;
    private final WeeklyReportFormatter formatter;
    private final String senderEmail;
    private final List<String> recipientEmails;

    @Inject
    public SesWeeklyReportSender(SesClient ses,
                                 WeeklyReportFormatter formatter,
                                 @ConfigProperty(name = "notification.sender-email") String senderEmail,
                                 @ConfigProperty(name = "notification.recipient-emails") List<String> recipientEmails) {
        this.ses = ses;
        this.formatter = formatter;
        this.senderEmail = senderEmail;
        this.recipientEmails = recipientEmails;
    }

    @Override
    public void send(WeeklyReport report, String reportLocation) {
        String body = formatter.format(report) + "\nFull report stored at: " + reportLocation + "\n";

        ses.sendEmail(SendEmailRequest.builder()
                .source(senderEmail)
                .destination(Destination.builder().toAddresses(recipientEmails).build())
                .message(Message.builder()
                        .subject(Content.builder().charset("UTF-8").data(formatter.subject(report)).build())
                        .body(Body.builder()
                                .text(Content.builder().charset("UTF-8").data(body).build())
                                .build())
                        .build())
                .build());
    }

}
