package com.examples.java.vertx.kafka.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;
import io.vertx.kafka.client.serialization.JsonObjectSerializer;


public class ProducerVerticle extends AbstractVerticle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProducerVerticle.class);
	
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new ProducerVerticle());
	}

	@Override
	public void start() throws Exception {
		Properties config = new Properties();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.4:9092");
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.ACKS_CONFIG, "1");
		KafkaProducer<String, String> producer = KafkaProducer.create(vertx, config);
		producer.partitionsFor("test", done -> {
			done.result().forEach(p -> LOGGER.info("Partition: id={}, topic={}", p.getPartition(), p.getTopic()));
		});
		
		Router router = Router.router(vertx);
		router.route("/order/*").handler(ResponseContentTypeHandler.create());
		router.route(HttpMethod.GET, "/order").handler(BodyHandler.create());
		router.get("/order").produces("application/json").handler(rc -> {
			//Order o = Json.decodeValue(rc.getBodyAsString(), Order.class);
			KafkaProducerRecord<String, String> record = KafkaProducerRecord.create("test", "key", "hello from vertx", 0);
			producer.write(record, done -> {
				if (done.succeeded()) {

					//RecordMetadata recordMetadata = done.result();
					LOGGER.info("Record sent: msg={}, destination={}, partition={}, offset={}", record.value() ); //, recordMetadata.getTopic(), recordMetadata.getPartition(), recordMetadata.getOffset());

				} else {
					Throwable t = done.cause();
					LOGGER.error("Error sent to topic: {}", t.getMessage());

				}
				rc.response().end("donesss");
			});
		});
		vertx.createHttpServer().requestHandler(router::accept).listen(8090);
		
	}
	
}