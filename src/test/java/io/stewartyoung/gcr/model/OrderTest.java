package io.stewartyoung.gcr.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;

public class OrderTest {

    @Test
    public void testOrder() {
        Order order = new Order(new BigDecimal(32000.10154), new BigDecimal(0.003));
        Assertions.assertTrue(order.getPrice().equals(new BigDecimal(32000.10154)));
        Assertions.assertTrue(order.getSize().equals(new BigDecimal(0.003)));
    }
}
