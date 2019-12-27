package com.examples.java.vertx.kafka.common;

public final class Constants {
    public static final String KAFKA_BOOTSTRAP_SERVER = System.getenv("KAFKA_BOOTSTRAP_SERVER");
    public static final String KAFKA_ORDERS_TOPIC = System.getenv("KAFKA_ORDERS_TOPIC");
}
