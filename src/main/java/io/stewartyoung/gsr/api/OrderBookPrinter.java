package io.stewartyoung.gsr.api;

import io.stewartyoung.gsr.model.OrderBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class OrderBookPrinter {
    private static final Logger LOG = LoggerFactory.getLogger(OrderBookPrinter.class);

    private static final int NUM_ORDERBOOK_LEVELS = 10;

    public static void print(OrderBook orderBook) {
        List<BigDecimal> topAsks = orderBook.getTopOrders(NUM_ORDERBOOK_LEVELS, "asks");
        List<BigDecimal> topBids = orderBook.getTopOrders(NUM_ORDERBOOK_LEVELS, "bids");
        LOG.info("Top asks: {}", topAsks);
        LOG.info("Top bids: {}", topBids);
    }
}
