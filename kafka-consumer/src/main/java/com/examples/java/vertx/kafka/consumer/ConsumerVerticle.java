package com.examples.java.vertx.kafka.consumer;

import com.examples.java.vertx.kafka.common.Constants;
import com.examples.java.vertx.kafka.common.Order;
import com.examples.java.vertx.kafka.common.OrderStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

public class ConsumerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerVerticle.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ConsumerVerticle());
    }

    @Override
    public void start() throws Exception {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.KAFKA_BOOTSTRAP_SERVER);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "order-shipper");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, config);
        consumer.subscribe(Constants.KAFKA_ORDERS_TOPIC, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Subscribed");
            } else {
                LOGGER.error("Could not subscribe: err={}", ar.cause().getMessage());
            }
        });

        consumer.handler(record -> {
            LOGGER.info("Processing: key={}, value={}, partition={}, offset={}", record.key(), record.value(), record.partition(), record.offset());
            Order order = Json.decodeValue(record.value(), Order.class);
            order.setStatus(OrderStatus.SHIPPED);
            order.setProcessedTime(Instant.now());
            //TODO: Add code to simulate a delay.
            LOGGER.info("Order processed: id={}, price={}, timeTaken={}", order.getId(), order.getPrice(), Duration.between(order.getCreatedTime(), order.getProcessedTime()).toMillis());
        });
    }
}
