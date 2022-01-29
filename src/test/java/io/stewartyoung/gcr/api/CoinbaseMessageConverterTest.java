package io.stewartyoung.gcr.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.stewartyoung.gcr.model.OrderBook;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoinbaseMessageConverterTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testConvertSnapshot() throws IOException {
        JsonNode testSnapshotJsonMessage = objectMapper.readTree(this.getClass().getClassLoader().getResource("ExampleSnapshot.json"));
        CoinbaseMessageConverter coinbaseMessageConverter = new CoinbaseMessageConverter();
        OrderBook testOrderBook = coinbaseMessageConverter.convertSnapshot(testSnapshotJsonMessage);
        TreeMap<BigDecimal, BigDecimal> askPriceAndSizeMap = new TreeMap<>();
        TreeMap<BigDecimal, BigDecimal> bidPriceAndSizeMap = new TreeMap<>(Comparator.reverseOrder());
        JsonNode testAsks = testSnapshotJsonMessage.get("asks");
        JsonNode testBids = testSnapshotJsonMessage.get("bids");
        for (JsonNode arr: testAsks) {
            askPriceAndSizeMap.put(new BigDecimal(arr.get(0).asText()), new BigDecimal(arr.get(1).asText()));
        }
        for (JsonNode arr: testBids) {
            bidPriceAndSizeMap.put(new BigDecimal(arr.get(0).asText()), new BigDecimal(arr.get(1).asText()));
        }

        assertEquals(askPriceAndSizeMap, testOrderBook.getAsks());
        assertEquals(bidPriceAndSizeMap, testOrderBook.getBids());
    }

    @Test
    public void testConvertL2() {

    }

    @Test
    public void testGetPriceAndSizeMap() {

    }
}
