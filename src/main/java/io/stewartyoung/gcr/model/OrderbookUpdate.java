package io.stewartyoung.gcr.model;

import lombok.Getter;

import java.util.List;

public class OrderbookUpdate {
    @Getter
    private final List<Order> bids;
    @Getter
    private final List<Order> asks;
    
    public OrderbookUpdate(List<Order> bids, List<Order> asks) {
        this.bids = bids;
        this.asks = asks;
    }
}
