package io.stewartyoung.orderbook.model;

import lombok.Getter;

import java.math.BigDecimal;

public class Order {
    @Getter
    private final BigDecimal price;
    @Getter
    private final BigDecimal size;

    /**
     * Model for a Coinbase Order.
     * @param price order price
     * @param size corresponding order size
     */
    public Order(BigDecimal price, BigDecimal size) {
        this.price = price;
        this.size = size;
    }
}
