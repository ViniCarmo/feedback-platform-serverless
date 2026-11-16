package org.com.vinicius.notifycritical.adapter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.gateway.CriticalFeedbackNotifier;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class SesCriticalFeedbackNotifier implements CriticalFeedbackNotifier {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.of("America/Sao_Paulo"));

    private final SesClient ses;
    private final String senderEmail;
    private final List<String> recipientEmails;

    @Inject
    public SesCriticalFeedbackNotifier(SesClient ses,
                                       @ConfigProperty(name = "notification.sender-email") String senderEmail,
                                       @ConfigProperty(name = "notification.recipient-emails") List<String> recipientEmails) {
        this.ses = ses;
        this.senderEmail = senderEmail;
        this.recipientEmails = recipientEmails;
    }

    @Override
    public void notifyCritical(Feedback feedback) {
        String subject = "[URGENT] New critical review received";
        String body = """
                A critical review has been received on the feedback platform.

                Description: %s
                Score: %d
                Urgency: %s
                Submission Date: %s (Brasília Time)
                Id: %s
                """.formatted(
                feedback.description(),
                feedback.score(),
                feedback.urgency(),
                DATE_FORMAT.format(feedback.submittedAt()),
                feedback.id());

        ses.sendEmail(SendEmailRequest.builder()
                .source(senderEmail)
                .destination(Destination.builder().toAddresses(recipientEmails).build())
                .message(Message.builder()
                        .subject(Content.builder().charset("UTF-8").data(subject).build())
                        .body(Body.builder()
                                .text(Content.builder().charset("UTF-8").data(body).build())
                                .build())
                        .build())
                .build());
}
}
