package org.com.vinicius.notifycritical.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.usecase.NotifyCriticalFeedbackUseCase;
import org.com.vinicius.notifycritical.dto.CriticalFeedbackEvent;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class NotifyCriticalFeedbackHandler implements RequestHandler<SQSEvent, SQSBatchResponse> {
    private static final Logger LOG = Logger.getLogger(NotifyCriticalFeedbackHandler.class);

    private final NotifyCriticalFeedbackUseCase notifyCriticalFeedback;
    private final ObjectMapper objectMapper;

    @Inject
    public NotifyCriticalFeedbackHandler(NotifyCriticalFeedbackUseCase notifyCriticalFeedback,
                                         ObjectMapper objectMapper) {
        this.notifyCriticalFeedback = notifyCriticalFeedback;
        this.objectMapper = objectMapper;
    }

    @Override
    public SQSBatchResponse handleRequest(SQSEvent event, Context context) {
        List<SQSBatchResponse.BatchItemFailure> failures = new ArrayList<>();

        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                Feedback feedback = objectMapper
                        .readValue(message.getBody(), CriticalFeedbackEvent.class)
                        .toDomain();
                boolean notified = notifyCriticalFeedback.execute(feedback);
                LOG.infof("Message %s processed: feedbackId=%s notified=%s",
                        message.getMessageId(), feedback.id(), notified);
            } catch (Exception e) {
                LOG.errorf(e, "Failed to process message %s", message.getMessageId());
                failures.add(new SQSBatchResponse.BatchItemFailure(message.getMessageId()));
            }
        }

        return new SQSBatchResponse(failures);
    }
}
