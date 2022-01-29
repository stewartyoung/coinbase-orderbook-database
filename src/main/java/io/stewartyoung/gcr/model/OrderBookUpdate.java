package io.stewartyoung.gcr.model;

import lombok.Getter;

import java.util.List;

public class OrderBookUpdate {
    @Getter
    private final List<Order> asks;
    @Getter
    private final List<Order> bids;

    public OrderBookUpdate(List<Order> asks, List<Order> bids) {
        this.asks = asks;
        this.bids = bids;
    }
}
