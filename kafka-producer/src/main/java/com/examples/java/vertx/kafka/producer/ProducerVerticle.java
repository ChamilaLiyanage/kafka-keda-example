package com.examples.java.vertx.kafka.producer;

import com.examples.java.vertx.kafka.common.Constants;
import com.examples.java.vertx.kafka.common.Order;
import com.examples.java.vertx.kafka.common.OrderStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;
import io.vertx.kafka.client.serialization.JsonObjectSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Properties;
import java.util.Random;


public class ProducerVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ProducerVerticle.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ProducerVerticle(), new DeploymentOptions().setWorker(true));
    }

    @Override
    public void start() throws Exception {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.KAFKA_BOOTSTRAP_SERVER);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonObjectSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "1");
        KafkaProducer<String, JsonObject> producer = KafkaProducer.create(vertx, config);
        producer.partitionsFor(Constants.KAFKA_ORDERS_TOPIC, done -> {
            done.result().forEach(p -> logger.info("Partition: id={}, topic={}", p.getPartition(), p.getTopic()));
        });

        Router router = Router.router(vertx);
        router.route("/orders/*").handler(ResponseContentTypeHandler.create());
        router.get("/orders/:orders").produces("application/json").handler(rc -> {
            String noOfOrders = rc.pathParam("orders");
            int loopMax = tryParseInt(noOfOrders);
            JsonArray orderList = new JsonArray();

            for (int i = 0; i < loopMax; i++) {
                Random random = new Random();
                Order order = new Order(String.valueOf(i), OrderStatus.NEW, random.nextDouble() * 1000, random.nextInt(100), Instant.now(), null);
                JsonObject orderJson = JsonObject.mapFrom(order);
                orderList.add(orderJson);
            }

            for (Object order : orderList) {
                JsonObject orderJson = JsonObject.mapFrom(order);
                //Distribute the messages evenly around the partitions.
                KafkaProducerRecord<String, JsonObject> record = KafkaProducerRecord.create(Constants.KAFKA_ORDERS_TOPIC,orderJson);
                producer.write(record, done -> {
                    if (done.succeeded()) {
                        RecordMetadata recordMetadata = done.result();
                        logger.info("Record sent: msg={}, destination={}, partition={}, offset={}", record.value(), recordMetadata.getTopic(), recordMetadata.getPartition(), recordMetadata.getOffset());

                    } else {
                        Throwable t = done.cause();
                        logger.error("Error sent to topic: {}", t.getMessage());
                    }
                });

            }
            rc.response().end(orderList.encodePrettily());
        });
        vertx.createHttpServer().requestHandler(router::accept).listen(8090);
    }

    private int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            // Log exception.
            return 10;
        }
    }
}
