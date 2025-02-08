package com.wbd.ams.producerconsumer.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Request object for publishing custom messages to Kafka")
public class CustomMessageRequest {

    @Schema(
            description = "Kafka topic name",
            example = "test-topic",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Topic cannot be blank")
    @Size(min = 1, max = 100, message = "Topic must be between 1 and 100 characters")
    private String topic;

    @Schema(
            description = "Message key prefix",
            example = "user-message",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Key cannot be blank")
    @Size(min = 1, max = 50, message = "Key must be between 1 and 50 characters")
    private String key;

    @Schema(
            description = "Message content prefix",
            example = "batch-message",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Message prefix cannot be blank")
    @Size(min = 1, max = 100, message = "Message prefix must be between 1 and 100 characters")
    private String messagePrefix;

    @Schema(
            description = "Start index for message generation",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Min(value = 1, message = "Start index must be at least 1")
    private int startIndex;

    @Schema(
            description = "End index for message generation",
            example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Min(value = 1, message = "End index must be at least 1")
    private int endIndex;

    public void validate() {
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("Start index must be less than or equal to end index");
        }
    }
}