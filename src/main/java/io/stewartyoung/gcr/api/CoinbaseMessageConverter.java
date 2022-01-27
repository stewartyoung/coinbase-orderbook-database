package io.stewartyoung.gcr.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.stewartyoung.gcr.model.Orderbook;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CoinbaseMessageConverter {

    public static Orderbook convertSnapshot(JsonNode snapshotJsonMessage) {
        Map<BigDecimal, BigDecimal> asks = getPriceAndSizeMap(snapshotJsonMessage.get("asks"));
        Map<BigDecimal, BigDecimal> bids = getPriceAndSizeMap(snapshotJsonMessage.get("bids"));
        
        return new Orderbook(asks, bids);
    }
    
    public static Map<BigDecimal, BigDecimal> getPriceAndSizeMap(JsonNode priceAndSizeArrayNode) {
        Map<BigDecimal, BigDecimal> priceAndSizeMap = new HashMap<>();
        for (JsonNode arr : priceAndSizeArrayNode) {
            BigDecimal price = new BigDecimal(arr.get(0).asText());
            BigDecimal size = new BigDecimal(arr.get(1).asText());
            priceAndSizeMap.put(price, size);
        }
        
        return priceAndSizeMap;
    }

}
