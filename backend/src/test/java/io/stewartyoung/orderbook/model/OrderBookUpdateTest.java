package io.stewartyoung.orderbook.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderBookUpdateTest {
    @Test
    public void testOrderBookUpdate() {
        List<Order> testAsks = new ArrayList<>();
        List<Order> testBids = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            testAsks.add(new Order(new BigDecimal(i * 102), new BigDecimal(i * 0.003)));
            testBids.add(new Order(new BigDecimal(i * 100), new BigDecimal(i * 0.003)));
        }
        OrderBookUpdate orderBookUpdate = new OrderBookUpdate(testAsks, testBids);

        for (int j = 0; j < testBids.size(); j++) {
            Assertions.assertEquals(0, testBids.get(j).getPrice().compareTo(orderBookUpdate.getBids().get(j).getPrice()));
            Assertions.assertEquals(0, 0, testBids.get(j).getSize().compareTo(orderBookUpdate.getBids().get(j).getSize()));
            Assertions.assertEquals(0, testAsks.get(j).getPrice().compareTo(orderBookUpdate.getAsks().get(j).getPrice()));
            Assertions.assertEquals(0, 0, testAsks.get(j).getSize().compareTo(orderBookUpdate.getAsks().get(j).getSize()));
        }
    }
}
