package org.com.vinicius.receivefeedback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record FeedbackRequest(@JsonProperty("descricao") String description,
                              @JsonProperty("nota") Integer score) {
}
