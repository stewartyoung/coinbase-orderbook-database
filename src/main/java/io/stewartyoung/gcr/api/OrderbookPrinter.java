package io.stewartyoung.gcr.api;

import io.stewartyoung.gcr.model.Orderbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class OrderbookPrinter {
    private static final Logger LOG = LoggerFactory.getLogger(OrderbookPrinter.class);

    private static final int NUM_LEVELS = 10;

    // TODO: return in a pretty webpage
    public static void print(Orderbook orderbook) {
        List<BigDecimal> topAsks = orderbook.getTopOrders(NUM_LEVELS, "asks");
        List<BigDecimal> topBids = orderbook.getTopOrders(NUM_LEVELS, "bids");
        LOG.info("Top asks: {}", topAsks);
        LOG.info("Top bids: {}", topBids);
    }
}
