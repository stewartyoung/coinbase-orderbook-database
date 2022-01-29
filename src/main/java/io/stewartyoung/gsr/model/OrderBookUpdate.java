package io.stewartyoung.gsr.model;

import lombok.Getter;

import java.util.List;

// TODO: Javadoc style comments

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
