package com.gateway.controllers;

import com.object.OrderKafka;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, OrderKafka> kafkaTemplate;

    @Value("${app.topic.order}")
    private String topic;

    void send(OrderKafka data) {
        log.info("sending data='{}'", data);
        kafkaTemplate.send(topic, data);
    }
}