package org.com.vinicius.receivefeedback.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.gateway.FeedbackEventPublisher;
import org.com.vinicius.receivefeedback.dto.CriticalFeedbackEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@ApplicationScoped
public class SnsFeedbackEventPublisher implements FeedbackEventPublisher {

    private final SnsClient sns;
    private final ObjectMapper objectMapper;
    private final String topicArn;

    @Inject
    public SnsFeedbackEventPublisher(SnsClient sns, ObjectMapper objectMapper, @ConfigProperty(name = "feedback.critical-topic-arn") String topicArn) {
        this.sns = sns;
        this.objectMapper = objectMapper;
        this.topicArn = topicArn;
    }


    @Override
    public void publishCritical(Feedback feedback) {
        try {
            String message = objectMapper.writeValueAsString(CriticalFeedbackEvent.from(feedback));
            sns.publish(PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .build());
        }catch (JsonProcessingException e){
            throw new IllegalStateException("Could not serialize critical feedback event", e);
        }

    }
}
