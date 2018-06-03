package com.statistic.controller;

import com.object.OrderKafka;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    MessageStorage storage;

    @KafkaListener(topics="${app.topic.order}")
    public void processMessage(OrderKafka content) {
        log.info("received content = '{}'", content);
        System.out.println("MESSAGE = " + content.toString());
        storage.put(content);
    }
}
