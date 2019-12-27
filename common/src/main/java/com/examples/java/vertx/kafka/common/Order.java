package com.examples.java.vertx.kafka.common;

import java.time.Instant;

public class Order {

    private String id;
    private OrderStatus status;
    private double price;
    private int quantity;
    private Instant createdTime;
    private Instant processedTime;

    public Order() {
    }

    public Order(String id, OrderStatus status, double price, int quantity, Instant createdTime, Instant processedTime) {
        this.id = id;
        this.status = status;
        this.price = price;
        this.quantity = quantity;
        this.createdTime = createdTime;
        this.processedTime = processedTime;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public Instant getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(Instant processedTime) {
        this.processedTime = processedTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}