package io.stewartyoung.gcr.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Orderbook {

    private final TreeMap<BigDecimal, BigDecimal> asks;
    private final TreeMap<BigDecimal, BigDecimal> bids;

    public Orderbook(TreeMap<BigDecimal, BigDecimal> asks, TreeMap<BigDecimal, BigDecimal> bids){
        // Using a TreeMap will sort the asks by price (the key) in ascending order
        this.asks = asks;
        // A Treemap will sort the bids by price (the key) in descending order
        this.bids = bids;
    }

    public void l2UpdateOrderBook(OrderbookUpdate orderbookUpdate) {
        List<Order> bidsList = orderbookUpdate.getBids();
        for (Order order : bidsList) {
            if (order.getSize().signum() == 0) {
                bids.remove(order.getPrice());
            } else {
                bids.put(order.getPrice(), order.getSize());
            }
        }

        List<Order> asksList = orderbookUpdate.getAsks();
        for (Order order : asksList) {
            if (order.getSize().signum() == 0) {
                asks.remove(order.getPrice());
            } else {
                asks.put(order.getPrice(), order.getSize());
            }
        }
    }

    public List<BigDecimal> getTopOrders(int numOrderbookLevels, String orderType) {
        TreeMap<BigDecimal, BigDecimal> orders = null;
        List<BigDecimal> topOrders = new ArrayList<>();

        if (orderType == "asks") {
            orders = asks;
        } else if (orderType == "bids") {
            orders = bids;
        }

        int count = 0;
        for (Map.Entry<BigDecimal, BigDecimal> entry : orders.entrySet()) {
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
