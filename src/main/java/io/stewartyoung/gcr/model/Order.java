package io.stewartyoung.gcr.model;

import lombok.Getter;

import java.math.BigDecimal;

public class Order {
    @Getter
    private final BigDecimal price;
    @Getter
    private final BigDecimal size;

    public Order(BigDecimal price, BigDecimal size) {
        this.price = price;
        this.size = size;
    }
}
