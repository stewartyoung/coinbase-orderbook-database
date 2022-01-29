package io.stewartyoung.gcr.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.stewartyoung.gcr.model.Order;
import io.stewartyoung.gcr.model.OrderBook;
import io.stewartyoung.gcr.model.OrderBookUpdate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoinbaseMessageConverterTest {

    private ObjectMapper objectMapper = new ObjectMapper();
    private OrderBook testOrderBook;
    private CoinbaseMessageConverter coinbaseMessageConverter;

    // TODO: arrange, act, assert

    @Test
    public void testConvertSnapshot() throws IOException {
        JsonNode testSnapshotJsonMessage = objectMapper.readTree(this.getClass().getClassLoader().getResource("ExampleSnapshot.json"));
        coinbaseMessageConverter = new CoinbaseMessageConverter();
        testOrderBook = coinbaseMessageConverter.convertSnapshot(testSnapshotJsonMessage);
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
    public void testConvertL2() throws IOException {
        JsonNode testL2BuyJsonMessage = objectMapper.readTree(this.getClass().getClassLoader().getResource("ExampleL2BuyUpdate.json"));
        JsonNode testL2SellJsonMessage = objectMapper.readTree(this.getClass().getClassLoader().getResource("ExampleL2SellUpdate.json"));

        OrderBookUpdate buyUpdate = coinbaseMessageConverter.convertL2(testL2BuyJsonMessage);
        List<Order> expectedBuyChanges = new ArrayList<>();
        expectedBuyChanges.add(new Order(new BigDecimal("36944.35"), new BigDecimal("0.00000000")));
        OrderBookUpdate expectedBuyUpdate = new OrderBookUpdate(new ArrayList<>(), expectedBuyChanges);
        assertEquals(0, expectedBuyUpdate.getBids().get(0).getPrice().compareTo(buyUpdate.getBids().get(0).getPrice()));

        OrderBookUpdate sellUpdate = coinbaseMessageConverter.convertL2(testL2SellJsonMessage);
        List<Order> expectedSellChanges = new ArrayList<>();
        expectedSellChanges.add(new Order(new BigDecimal("36991.04"), new BigDecimal("0.00000000")));
        OrderBookUpdate expectedSellUpdate = new OrderBookUpdate(expectedSellChanges, new ArrayList<>());
        assertEquals(0, expectedSellUpdate.getAsks().get(0).getPrice().compareTo(sellUpdate.getAsks().get(0).getPrice()));
    }

    @Test
    public void testGetPriceAndSizeMap() throws IOException {
        JsonNode testSnapshotJsonMessage = objectMapper.readTree(this.getClass().getClassLoader().getResource("ExampleSnapshot.json"));
        TreeMap<BigDecimal, BigDecimal> asks = coinbaseMessageConverter.getPriceAndSizeMap(testSnapshotJsonMessage.get("asks"), "asks");
        TreeMap<BigDecimal, BigDecimal> bids = coinbaseMessageConverter.getPriceAndSizeMap(testSnapshotJsonMessage.get("bids"), "bids");

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

        assertTrue(asks.equals(askPriceAndSizeMap));
        assertTrue(bids.equals(bidPriceAndSizeMap));
    }
}
