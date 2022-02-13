package io.stewartyoung.orderbook.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.stewartyoung.orderbook.model.Order;
import io.stewartyoung.orderbook.model.OrderBook;
import io.stewartyoung.orderbook.model.OrderBookUpdate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class CoinbaseMessageConverter {
    /**
     * Converts a "snapshot" json message from the Coinbase websocket feed into the internal model OrderBook of asks and bids.
     * @param snapshotJsonMessage a "snapshot" json message from the Coinbase websocket feed
     * @return OrderBook of asks and bids
     */
    public static OrderBook convertSnapshot(JsonNode snapshotJsonMessage) {
        TreeMap<BigDecimal, BigDecimal> asks = getPriceAndSizeMap(snapshotJsonMessage.get("asks"), "asks");
        TreeMap<BigDecimal, BigDecimal> bids = getPriceAndSizeMap(snapshotJsonMessage.get("bids"), "bids");
        
        return new OrderBook(asks, bids);
    }

    /**
     * Converts an "l2" json message from the Coinbase websocket feed into the internal model OrderBookUpdate
     * which contains sellChanges and buyChanges.
     * @param l2JsonMessage a "snapshot" json message from the Coinbase websocket feed
     * @return OrderBookUpdate of sellChanges and buyChanges, which update OrderBook asks and bids
     */
    public static OrderBookUpdate convertL2(JsonNode l2JsonMessage) {
        JsonNode changes = l2JsonMessage.get("changes").get(0);
        List<Order> buyChanges = new ArrayList<>();
        List<Order> sellChanges = new ArrayList<>();

        String side = changes.get(0).asText();
        BigDecimal price = new BigDecimal(changes.get(1).asText());
        BigDecimal size = new BigDecimal(changes.get(2).asText());
        Order order = new Order(price, size);

        switch (side) {
            case "buy":
                buyChanges.add(order);
                break;
            case "sell":
                sellChanges.add(order);
                break;
        }

        return new OrderBookUpdate(sellChanges, buyChanges);
    }

    /**
     * Takes either asks or bids array from snapshot message and makes a TreeMap of price and size.
     * @param priceAndSizeArrayNode an array containing orders of price and corresponding size
     * @param orderType either "asks" or "bids" array
     * @return
     */
    public static TreeMap<BigDecimal, BigDecimal> getPriceAndSizeMap(JsonNode priceAndSizeArrayNode, String orderType) {
        TreeMap<BigDecimal, BigDecimal> priceAndSizeMap = null;
        if (orderType.equals("asks")) {
            priceAndSizeMap = new TreeMap<>();
        } else if (orderType.equals("bids")){
            priceAndSizeMap = new TreeMap<>(Comparator.reverseOrder());
        }
        for (JsonNode arr : priceAndSizeArrayNode) {
            BigDecimal price = new BigDecimal(arr.get(0).asText());
            BigDecimal size = new BigDecimal(arr.get(1).asText());
            priceAndSizeMap.put(price, size);
        }
        
        return priceAndSizeMap;
    }

}
