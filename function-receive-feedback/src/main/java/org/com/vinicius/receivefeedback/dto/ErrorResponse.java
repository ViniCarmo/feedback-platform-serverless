package org.com.vinicius.receivefeedback.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ErrorResponse(String message) {
}
