package io.stewartyoung.gcr.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Orderbook {
    // TODO: Can set to treemap initially in converter
    private final TreeMap<BigDecimal, BigDecimal> sortedAsks;
    private final TreeMap<BigDecimal, BigDecimal> sortedBids;

    public Orderbook(Map<BigDecimal, BigDecimal> asks, Map<BigDecimal, BigDecimal> bids){
        // Using a TreeMap will sort the asks by price (the key) in ascending order
        sortedAsks = new TreeMap<>(asks);
        // A Treemap will sort the bids by price (the key) in descending order
        sortedBids = new TreeMap<>(Comparator.reverseOrder());
        sortedBids.putAll(bids);
    }

    public void l2UpdateOrderBook(OrderbookUpdate orderbookUpdate) {
        List<Order> bids = orderbookUpdate.getBids();
        for (Order order : bids) {
            if (order.getSize().signum() == 0) {
                sortedBids.remove(order.getPrice());
            } else {
                sortedBids.put(order.getPrice(), order.getSize());
            }
        }

        List<Order> asks = orderbookUpdate.getAsks();
        for (Order order : asks) {
            if (order.getSize().signum() == 0) {
                sortedAsks.remove(order.getPrice());
            } else {
                sortedAsks.put(order.getPrice(), order.getSize());
            }
        }
    }

    public List<BigDecimal> getTopOrders(int numOrderbookLevels, String orderType) {
        TreeMap<BigDecimal, BigDecimal> sortedOrders = null;
        List<BigDecimal> topOrders = new ArrayList<>();

        if (orderType == "asks") {
            sortedOrders = sortedAsks;
        } else if (orderType == "bids") {
            sortedOrders = sortedBids;
        }

        int count = 0;
        for (Map.Entry<BigDecimal, BigDecimal> entry : sortedOrders.entrySet()) {
            if (count < numOrderbookLevels) {
                topOrders.add(entry.getKey());
                count++;
            } else {
                break;
            }
        }

        return topOrders;
    }
}
