package io.stewartyoung.gsr.websockets;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CoinbaseWebsocketClientEndpointTest {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(CoinbaseWebsocketClientEndpointTest.class);
    private CoinbaseWebsocketClientEndpoint coinbaseWebsocketClientEndpoint;
    private List<ILoggingEvent> logsList;

    @Test
    public void testSubscribe() throws ExecutionException, InterruptedException {


        String testInstrument = "BTC_USD";
        String l2SubscribeMessageForBtcUsd = "{\"product_ids\":[\"BTC_USD\"],\"channels\":[\"level2\",\"heartbeat\",{\"product_ids\":[\"BTC_USD\"],\"name\":\"ticker\"}],\"type\":\"subscribe\"}";

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Runnable runnableTask = () -> {
            Logger coinbaseWebsocketClientEndpointLogger = (Logger) LoggerFactory.getLogger(CoinbaseWebsocketClientEndpoint.class);
            ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
            listAppender.start();

            coinbaseWebsocketClientEndpointLogger.addAppender(listAppender);
            coinbaseWebsocketClientEndpoint = new CoinbaseWebsocketClientEndpoint();
            logsList = listAppender.list;
            coinbaseWebsocketClientEndpoint.subscribe(testInstrument);
        };

        Future future = executorService.submit(runnableTask);
        try {
            LOG.info("Starting testing of CoinbaseWebsocketClientEndpoint.subscribe() with future");
            LOG.info(future.get(2, TimeUnit.SECONDS).toString()); // throws TimeoutException after 2 secs of executing subscribe
            // subscribe should not finish before 2 secs, websocket feed lasts until interrupted
            LOG.info("Finished executing future");
        } catch (TimeoutException | NullPointerException e) {
            future.cancel(true);
            LOG.info("Terminated CoinbaseWebsocketClientEndpoint.subscribe() future");
            boolean check = logsList.get(0).getFormattedMessage().contains("Subscribing to instrument: " + testInstrument);
            LOG.info("First line of logs: \"Subscribing to instrument: " + testInstrument + "\" is called? " + check);
            Assertions.assertTrue(logsList.get(0).getFormattedMessage().contains("Subscribing to instrument: " + testInstrument));

            Assertions.assertTrue(logsList.get(1).getFormattedMessage().contains("Subscribe message: " + l2SubscribeMessageForBtcUsd));
        }
    }
}
