package io.stewartyoung.gsr.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.stewartyoung.gsr.model.Order;
import io.stewartyoung.gsr.model.OrderBook;
import io.stewartyoung.gsr.model.OrderBookUpdate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class CoinbaseMessageConverter {

    public static OrderBook convertSnapshot(JsonNode snapshotJsonMessage) {
        TreeMap<BigDecimal, BigDecimal> asks = getPriceAndSizeMap(snapshotJsonMessage.get("asks"), "asks");
        TreeMap<BigDecimal, BigDecimal> bids = getPriceAndSizeMap(snapshotJsonMessage.get("bids"), "bids");
        
        return new OrderBook(asks, bids);
    }

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
