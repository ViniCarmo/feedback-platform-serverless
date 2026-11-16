package org.com.vinicius.receivefeedback.adapter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.com.vinicius.domain.entity.Feedback;
import org.com.vinicius.domain.gateway.FeedbackRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

@ApplicationScoped
public class DynamoFeedbackRepository implements FeedbackRepository {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Sao_Paulo");

    private final DynamoDbClient dynamoDb;
    private final String tableName;

    @Inject
    public DynamoFeedbackRepository(DynamoDbClient dynamoDb, @ConfigProperty(name = "feedback.table.name") String tableName) {
        this.dynamoDb = dynamoDb;
        this.tableName = tableName;
    }

    @Override
    public void save(Feedback feedback) {
        Map<String, AttributeValue> item = Map.of(
                "id", AttributeValue.fromS(feedback.id().toString()),
                "description", AttributeValue.fromS(feedback.description()),
                "score", AttributeValue.fromN(String.valueOf(feedback.score())),
                "urgency", AttributeValue.fromS(feedback.urgency().name()),
                "submittedAt", AttributeValue.fromS(feedback.submittedAt().toString()),
                "submittedDate", AttributeValue.fromS(
                        LocalDate.ofInstant(feedback.submittedAt(), BUSINESS_ZONE).toString())
        );
        dynamoDb.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .conditionExpression("attribute_not_exists(id)")
                .build());

    }
}
