package com.wbd.ams.producerconsumer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

@Service
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(id = "ss-test-cg-test1",
            topics = "${spring.kafka.consumer.topic-name}",
            groupId = "ss-test-cg-test1")
    public void listen(String value,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key,
                       @Header(KafkaHeaders.OFFSET) Long offset,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition) {
        try {
            logger.info("Received message - Topic: {}, Key: {}", topic, key);
            logger.info("Message value: {}", value);
            logger.info("Offset {} and Partition {}", offset,partition);

        } catch (Exception e) {
            logger.error("Error processing message - Topic: {}, Key: {}", topic, key, e);
        }
    }
}