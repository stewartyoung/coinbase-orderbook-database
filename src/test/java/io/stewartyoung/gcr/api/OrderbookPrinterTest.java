package io.stewartyoung.gcr.api;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.stewartyoung.gcr.model.OrderBook;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderbookPrinterTest {

    @Test
    public void testPrint() {
        TreeMap<BigDecimal, BigDecimal> testAsks = new TreeMap<>();
        TreeMap<BigDecimal, BigDecimal> testBids = new TreeMap<>();
        for (int i = 1; i < 10; i++) {
            // put asks in descending order to make sure printed in ascending by price
            testAsks.put(new BigDecimal((11-i) * 100), new BigDecimal(i * 0.003));
            // put bids in ascending order to make sure printed in descending by price
            testBids.put(new BigDecimal(i * 100), new BigDecimal(i * 0.003));
        }

        OrderBook testOrderbook = new OrderBook(testAsks, testBids);
        OrderBookPrinter orderBookPrinter = new OrderBookPrinter();

        Logger orderBookPrinterLogger = (Logger) LoggerFactory.getLogger(OrderBookPrinter.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        orderBookPrinterLogger.addAppender(listAppender);
        orderBookPrinter.print(testOrderbook);
        List<ILoggingEvent> logsList = listAppender.list;
        TreeMap<BigDecimal, BigDecimal> expectedBids = new TreeMap<>(Collections.reverseOrder());
        expectedBids.putAll(testBids);

        String stringTestAsks = String.join(", ",
            testAsks.keySet().stream().map(
                    bigDecimal -> bigDecimal.toString()
            ).toArray(String[]::new));
        String stringTestBids = String.join(", ",
                expectedBids.keySet().stream().map(
                        bigDecimal -> bigDecimal.toString()
                ).toArray(String[]::new));
        assertTrue(logsList.get(0).getFormattedMessage().contains(stringTestAsks));
        assertTrue(logsList.get(1).getFormattedMessage().contains(stringTestBids));
    }
}
