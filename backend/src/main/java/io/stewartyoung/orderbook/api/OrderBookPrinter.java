package io.stewartyoung.orderbook.api;

import io.stewartyoung.orderbook.model.OrderBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class OrderBookPrinter {
    private static final Logger LOG = LoggerFactory.getLogger(OrderBookPrinter.class);
    private static final int NUM_ORDERBOOK_LEVELS = 10;

    /**
     * Prints 10 levels of bid and asks from the orderbook to the console.
     * @param orderBook instance of Orderbook
     */
    public static void print(OrderBook orderBook) {
        List<BigDecimal> topAsks = orderBook.getTopOrders(NUM_ORDERBOOK_LEVELS, "asks");
        List<BigDecimal> topBids = orderBook.getTopOrders(NUM_ORDERBOOK_LEVELS, "bids");
        LOG.info("Top asks: {}", topAsks);
        LOG.info("Top bids: {}", topBids);
    }
}
