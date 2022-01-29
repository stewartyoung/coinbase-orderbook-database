package io.stewartyoung.gcr.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderBookTest {

    @Test
    public void testL2UpdateOrderBook() {

    }

    @Test
    public void testGetTopOrders() {
        TreeMap<BigDecimal, BigDecimal> testAsks = new TreeMap<>();
        TreeMap<BigDecimal, BigDecimal> testBids = new TreeMap<>(Comparator.reverseOrder());
        for (int i = 1; i < 20; i++) {
            // put asks in descending order to make sure printed in ascending by price
            testAsks.put(new BigDecimal((21-i) * 100), new BigDecimal(i * 0.003));
            // put bids in ascending order to make sure printed in descending by price
            testBids.put(new BigDecimal(i * 100), new BigDecimal(i * 0.003));
        }

        OrderBook orderBook = new OrderBook(testAsks, testBids);
        List<BigDecimal> actualAsks = orderBook.getTopOrders(10, "asks");
        List<BigDecimal> actualBids = orderBook.getTopOrders(10, "bids");

        List<BigDecimal> expectedAsks = testAsks.keySet().stream().limit(10).collect(Collectors.toList());
        List<BigDecimal> expectedBids = testBids.keySet().stream().limit(10).collect(Collectors.toList());

        assertEquals(actualAsks, expectedAsks);
        assertEquals(actualBids, expectedBids);

    }
}
