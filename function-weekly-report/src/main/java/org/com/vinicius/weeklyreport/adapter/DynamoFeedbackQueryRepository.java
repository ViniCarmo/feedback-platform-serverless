package org.com.vinicius.weeklyreport.adapter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.entity.Urgency;
import org.com.vinicius.domain.gateway.FeedbackQueryRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class DynamoFeedbackQueryRepository implements FeedbackQueryRepository {

    private static final String INDEX_NAME = "by-submitted-date";

    private final DynamoDbClient dynamoDb;
    private final String tableName;

    @Inject
    public DynamoFeedbackQueryRepository(DynamoDbClient dynamoDb,
                                         @ConfigProperty(name = "feedback.table-name") String tableName) {
        this.dynamoDb = dynamoDb;
        this.tableName = tableName;
    }

    @Override
    public List<Feedback> findBySubmittedDateBetween(LocalDate startInclusive, LocalDate endInclusive) {
        List<Feedback> feedbacks = new ArrayList<>();

        for (LocalDate day = startInclusive; !day.isAfter(endInclusive); day = day.plusDays(1)) {
            QueryRequest request = QueryRequest.builder()
                    .tableName(tableName)
                    .indexName(INDEX_NAME)
                    .keyConditionExpression("submittedDate = :day")
                    .expressionAttributeValues(Map.of(":day", AttributeValue.fromS(day.toString())))
                    .build();

            dynamoDb.queryPaginator(request).items()
                    .forEach(item -> feedbacks.add(toFeedback(item)));
        }

        return feedbacks;
    }

    private Feedback toFeedback(Map<String, AttributeValue> item) {
        return new Feedback(
                UUID.fromString(item.get("id").s()),
                item.get("description").s(),
                Integer.parseInt(item.get("score").n()),
                Urgency.valueOf(item.get("urgency").s()),
                Instant.parse(item.get("submittedAt").s()));
    }
}
