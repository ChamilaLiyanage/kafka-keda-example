package com.examples.java.vertx.kafka.common;

public class Order {

    private String id;
    private OrderStatus status;
    private double price;
    private int quantity;

    public Order(String id, OrderStatus status, double price, int quantity) {
        this.id = id;
        this.status = status;
        this.price = price;
        this.quantity = quantity;
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