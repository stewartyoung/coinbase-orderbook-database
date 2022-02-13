package io.stewartyoung.orderbook.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OrderBook {
    @Getter
    private final TreeMap<BigDecimal, BigDecimal> asks;
    @Getter
    private final TreeMap<BigDecimal, BigDecimal> bids;

    /**
     * Model for OrderBook of Orders.
     * @param asks contains a TreeMap of asks prices and sizes
     * @param bids contains a TreeMap of bids prices and sizes
     */
    public OrderBook(TreeMap<BigDecimal, BigDecimal> asks, TreeMap<BigDecimal, BigDecimal> bids){
        // Using a TreeMap will sort the asks by price (the key) in ascending order
        this.asks = asks;
        // A Treemap with reverseOrder comparator will sort the bids by price (the key) in descending order
        this.bids = new TreeMap<>(Collections.reverseOrder());
        this.bids.putAll(bids);
    }

    /**
     * Updates Orderbook using an OrderBookUpdate generated from l2 message.
     * @param orderBookUpdate an OrderBookUpdate generated from l2 message.
     */
    public void l2UpdateOrderBook(OrderBookUpdate orderBookUpdate) {
        List<Order> asksList = orderBookUpdate.getAsks();
        for (Order order : asksList) {
            if (order.getSize().signum() == 0) {
                asks.remove(order.getPrice());
            } else {
                asks.put(order.getPrice(), order.getSize());
            }
        }

        List<Order> bidsList = orderBookUpdate.getBids();
        for (Order order : bidsList) {
            if (order.getSize().signum() == 0) {
                bids.remove(order.getPrice());
            } else {
                bids.put(order.getPrice(), order.getSize());
            }
        }
    }

    /**
     * Gets the top orders from an OrderBook asks or bids list given numOrderBookLevels.
     * @param numOrderBookLevels Number of levels desired for the OrderBook command line print
     * @param orderType OrderBook asks or bids to use
     * @return
     */
    public List<BigDecimal> getTopOrders(int numOrderBookLevels, String orderType) {
        TreeMap<BigDecimal, BigDecimal> orders = null;
        List<BigDecimal> topOrders = new ArrayList<>();

        if (orderType == "asks") {
            orders = asks;
        } else if (orderType == "bids") {
            orders = bids;
        }

        // TODO: sanitise inputs, check for invalid params
        if (orders.size() < numOrderBookLevels) {
            throw new IllegalArgumentException("The number of orderbook levels " + numOrderBookLevels + " exceeds the number of orders " + orders.size());
        }

        int count = 0;
        for (Map.Entry<BigDecimal, BigDecimal> entry : orders.entrySet()) {
            if (count < numOrderBookLevels) {
                topOrders.add(entry.getKey());
                count++;
            } else {
                break;
            }
        }

        return topOrders;
    }
}
