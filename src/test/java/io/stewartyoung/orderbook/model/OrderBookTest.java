package io.stewartyoung.orderbook.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderBookTest {

    @Test
    public void testL2UpdateOrderBook() {
        // this should cancel the order for the top ask and top bid to check it's removal
        TreeMap<BigDecimal, BigDecimal> initialAsks = new TreeMap<>();
        TreeMap<BigDecimal, BigDecimal> initialBids = new TreeMap<>();
        List<Order> l2Asks = new ArrayList<>();
        List<Order> l2Bids = new ArrayList<>();
        for (int i = 1; i < 20; i++) {
            if (i == 19) {
                // set size equal to zero for smallest ask
                l2Asks.add(new Order(new BigDecimal((21-i) * 100), BigDecimal.ZERO));
                // set size equal to zero for largest bid
                l2Bids.add(new Order(new BigDecimal(i * 100), BigDecimal.ZERO));
            } else {
                l2Asks.add(new Order(new BigDecimal((21-i) * 100),new BigDecimal(i * 0.003)));
                l2Bids.add(new Order(new BigDecimal(i * 100), new BigDecimal(i * 0.003)));
            }
            initialAsks.put(new BigDecimal((21-i) * 100), new BigDecimal(i * 0.003));
            initialBids.put(new BigDecimal(i * 100), new BigDecimal(i * 0.003));
        }

        OrderBook orderBook = new OrderBook(initialAsks, initialBids);
        OrderBookUpdate orderBookUpdate = new OrderBookUpdate(l2Asks, l2Bids);
        orderBook.l2UpdateOrderBook(orderBookUpdate);

        TreeMap<BigDecimal, BigDecimal> expectedL2UpdatedAsks = new TreeMap<>(initialAsks);
        TreeMap<BigDecimal, BigDecimal> expectedL2UpdatedBids = new TreeMap<>(initialBids);
        for (Order order : orderBookUpdate.getAsks()) {
            if (order.getSize().signum() == 0) {
                expectedL2UpdatedAsks.remove(order.getPrice());
            } else {
                expectedL2UpdatedAsks.put(order.getPrice(), order.getSize());
            }
        }
        for (Order order : orderBookUpdate.getBids()) {
            if (order.getSize().signum() == 0) {
                expectedL2UpdatedBids.remove(order.getPrice());
            } else {
                expectedL2UpdatedBids.put(order.getPrice(), order.getSize());
            }
        }

        assertEquals(orderBook.getAsks(), expectedL2UpdatedAsks);
        assertEquals(orderBook.getBids(), expectedL2UpdatedBids);
    }

    @Test
    public void testGetTopOrders() {
        TreeMap<BigDecimal, BigDecimal> testAsks = new TreeMap<>();
        TreeMap<BigDecimal, BigDecimal> testBids = new TreeMap<>(Comparator.reverseOrder());
        for (int i = 1; i < 20; i++) {
            testAsks.put(new BigDecimal((21-i) * 100), new BigDecimal(i * 0.003));
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
