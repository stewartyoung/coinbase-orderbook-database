package io.stewartyoung.gsr.model;

import lombok.Getter;
import java.util.List;

public class OrderBookUpdate {
    @Getter
    private final List<Order> asks;
    @Getter
    private final List<Order> bids;

    /**
     * Model for an OrderBookUpdate, using List&lt;Order&gt; to represent asks and bids.
     * @param asks updates on the ask side of OrderBook
     * @param bids updates on the bids side of OrderBook
     */
    public OrderBookUpdate(List<Order> asks, List<Order> bids) {
        this.asks = asks;
        this.bids = bids;
    }
}
