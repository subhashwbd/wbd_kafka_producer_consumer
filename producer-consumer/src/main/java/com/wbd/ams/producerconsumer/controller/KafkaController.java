package com.wbd.ams.producerconsumer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.wbd.ams.producerconsumer.service.KafkaProducer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/kafka")
@Tag(name = "Kafka Message Publisher", description = "APIs for publishing messages to Kafka topics")
public class KafkaController {

    private static final Logger logger = LoggerFactory.getLogger(KafkaController.class);

    private final KafkaProducer kafkaProducer;

    public KafkaController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/publish/batch")
    @Operation(
            summary = "Publish Batch Messages",
            description = "Publish multiple messages to a Kafka topic with a common prefix",
            tags = {"Batch Publishing"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Messages published successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid number of messages",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> publishMessages(
            @Parameter(description = "Kafka topic name", required = true, example = "test-topic")
            @RequestParam String topic,

            @Parameter(description = "Message key prefix", required = true, example = "batch-key")
            @RequestParam String key,

            @Parameter(description = "Message content prefix", required = true, example = "batch-message")
            @RequestParam String messagePrefix,

            @Parameter(description = "Number of messages to publish", required = true, example = "5")
            @RequestParam int numberOfMessages) {

        logger.info("Attempting to publish batch messages - Topic: {}, Number of Messages: {}", topic, numberOfMessages);

        if (numberOfMessages <= 0) {
            logger.warn("Invalid number of messages requested: {}", numberOfMessages);
            return ResponseEntity.badRequest().body("Number of messages must be greater than 0");
        }

        Map<String, Object> response = new HashMap<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        try {
            for (int i = 0; i < numberOfMessages; i++) {
                String message = String.format("%s-%d", messagePrefix, i + 1);
                String messageKey = String.format("%s-%d", key, i + 1);

                try {
                    kafkaProducer.sendMessageAsync(topic, messageKey, message);
                    successCount.incrementAndGet();
                    logger.debug("Successfully published message: {}", message);
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    logger.error("Failed to publish message: {}", message, e);
                }
            }

            response.put("totalRequested", numberOfMessages);
            response.put("successCount", successCount.get());
            response.put("failureCount", failureCount.get());
            response.put("status", "Completed");

            logger.info("Batch message publishing completed - Success: {}, Failure: {}",
                    successCount.get(), failureCount.get());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Unexpected error during batch message publishing", e);
            response.put("error", e.getMessage());
            response.put("successCount", successCount.get());
            response.put("failureCount", failureCount.get());
            response.put("status", "Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/publish/single")
    @Operation(
            summary = "Publish Single Message",
            description = "Publish a single message to a Kafka topic",
            tags = {"Single Message Publishing"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Message published successfully",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string")
                            )
                    )
            }
    )
    public ResponseEntity<String> publishSingleMessage(
            @Parameter(description = "Kafka topic name", required = true, example = "test-topic")
            @RequestParam String topic,

            @Parameter(description = "Message key", required = true, example = "single-key")
            @RequestParam String key,

            @Parameter(description = "Message content", required = true, example = "Hello Kafka")
            @RequestParam String message) {

        logger.info("Attempting to publish single message - Topic: {}, Key: {}", topic, key);

        try {
            kafkaProducer.sendMessageAsync(topic, key, message);
            logger.info("Successfully published single message to topic: {}", topic);
            return ResponseEntity.ok("Message sent to Kafka");
        } catch (Exception e) {
            logger.error("Failed to send single message to topic: {}", topic, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send message: " + e.getMessage());
        }
    }

    @PostMapping("/publish/custom")
    @Operation(
            summary = "Publish Custom Messages",
            description = "Publish a range of messages to a Kafka topic with custom configuration",
            tags = {"Custom Message Publishing"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Messages published successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> publishCustomMessages(
            @Parameter(description = "Custom message request details", required = true)
            @Valid @RequestBody CustomMessageRequest request) {

        logger.info("Attempting to publish custom messages - Topic: {}, Start Index: {}, End Index: {}",
                request.getTopic(), request.getStartIndex(), request.getEndIndex());

        request.validate();

        Map<String, Object> response = new HashMap<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        try {
            for (int i = request.getStartIndex(); i <= request.getEndIndex(); i++) {
                String message = String.format("%s-%d", request.getMessagePrefix(), i);
                String messageKey = String.format("%s-%d", request.getKey(), i);

                try {
                    kafkaProducer.sendMessageAsync(request.getTopic(), messageKey, message);
                    successCount.incrementAndGet();
                    logger.debug("Successfully published custom message: {}", message);
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    logger.error("Failed to publish custom message: {}", message, e);
                }
            }

            response.put("totalRequested", request.getEndIndex() - request.getStartIndex() + 1);
            response.put("successCount", successCount.get());
            response.put("failureCount", failureCount.get());
            response.put("status", "Completed");

            logger.info("Custom message publishing completed - Success: {}, Failure: {}",
                    successCount.get(), failureCount.get());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unexpected error during custom message publishing", e);
            response.put("error", e.getMessage());
            response.put("status", "Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}