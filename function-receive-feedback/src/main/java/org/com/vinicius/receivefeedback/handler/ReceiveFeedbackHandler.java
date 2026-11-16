package org.com.vinicius.receivefeedback.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.exception.InvalidFeedbackException;
import org.com.vinicius.domain.usecase.RegisterFeedbackUseCase;
import org.com.vinicius.receivefeedback.dto.ErrorResponse;
import org.com.vinicius.receivefeedback.dto.FeedbackRequest;
import org.com.vinicius.receivefeedback.dto.FeedbackResponse;
import org.jboss.logging.Logger;

import org.jboss.logging.Logger;
import java.util.Map;


@Named("receiveFeedback")
public class ReceiveFeedbackHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOG = Logger.getLogger(ReceiveFeedbackHandler.class);
    private static final Map<String, String> JSON_HEADERS = Map.of("Content-Type", "application/json");

    private final RegisterFeedbackUseCase registerFeedback;
    private final ObjectMapper objectMapper;

    @Inject
    public ReceiveFeedbackHandler(RegisterFeedbackUseCase registerFeedback, ObjectMapper objectMapper) {
        this.registerFeedback = registerFeedback;
        this.objectMapper = objectMapper;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            FeedbackRequest request = parseBody(event.getBody());
            Feedback feedback = registerFeedback.execute(request.description(), request.score());
            LOG.infof("Feedback registered: id=%s urgency=%s", feedback.id(), feedback.urgency());
            return respond(201, FeedbackResponse.from(feedback));
        } catch (InvalidFeedbackException e) {
            LOG.warnf("Invalid feedback rejected: %s", e.getMessage());
            return respond(400, new ErrorResponse(e.getMessage()));
        }
    }

    private FeedbackRequest parseBody(String body) {
        if (body == null || body.isBlank()) {
            throw new InvalidFeedbackException("Request body is required");
        }
        try {
            FeedbackRequest request = objectMapper.readValue(body, FeedbackRequest.class);
            if (request.score() == null) {
                throw new InvalidFeedbackException("Field 'nota' is required");
            }
            return request;
        } catch (JsonProcessingException e) {
            throw new InvalidFeedbackException("Request body must be a valid JSON");
        }
    }

    private APIGatewayProxyResponseEvent respond(int statusCode, Object body) {
        try {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(statusCode)
                    .withHeaders(JSON_HEADERS)
                    .withBody(objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize response", e);
        }
    }
}
