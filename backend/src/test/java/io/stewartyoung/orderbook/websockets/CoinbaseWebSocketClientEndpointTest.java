package io.stewartyoung.orderbook.websockets;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CoinbaseWebSocketClientEndpointTest {

    @Mock
    protected WebSocketClientEndpoint mockWebSocketClientEndpoint;

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(CoinbaseWebSocketClientEndpointTest.class);
    private CoinbaseWebSocketClientEndpoint coinbaseWebSocketClientEndpoint;
    private List<ILoggingEvent> logsList;

    @BeforeEach
    public void setup() {
        coinbaseWebSocketClientEndpoint = new CoinbaseWebSocketClientEndpoint();
    }


    @Test
    public void testSubscribe() throws ExecutionException, InterruptedException {
        String testInstrument = "BTC_USD";
        String l2SubscribeMessageForBtcUsd = "{\"product_ids\":[\"BTC_USD\"],\"channels\":[\"level2\",\"heartbeat\",{\"product_ids\":[\"BTC_USD\"],\"name\":\"ticker\"}],\"type\":\"subscribe\"}";

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Runnable runnableTask = () -> {
            Logger coinbaseWebSocketClientEndpointLogger = (Logger) LoggerFactory.getLogger(CoinbaseWebSocketClientEndpoint.class);
            ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
            listAppender.start();

            coinbaseWebSocketClientEndpointLogger.addAppender(listAppender);
            logsList = listAppender.list;
            coinbaseWebSocketClientEndpoint.subscribe(testInstrument);
        };

        Future future = executorService.submit(runnableTask);
        try {
            LOG.info("Starting testing of CoinbaseWebSocketClientEndpoint.subscribe() with future");
            LOG.info(future.get(2, TimeUnit.SECONDS).toString()); // throws TimeoutException after 2 secs of executing subscribe
            // subscribe should not finish before 2 secs, websocket feed lasts until interrupted
            LOG.info("Finished executing future");
        } catch (TimeoutException | NullPointerException e) {
            future.cancel(true);
            LOG.info("Terminated CoinbaseWebSocketClientEndpoint.subscribe() future");
            boolean check = logsList.get(0).getFormattedMessage().contains("Subscribing to instrument: " + testInstrument);
            LOG.info("First line of logs: \"Subscribing to instrument: " + testInstrument + "\" is called? " + check);
            assertTrue(logsList.get(0).getFormattedMessage().contains("Subscribing to instrument: " + testInstrument));

            assertTrue(logsList.get(1).getFormattedMessage().contains("Subscribe message: " + l2SubscribeMessageForBtcUsd));
        }
    }

    @Test
    public void testHandleCoinbaseMessageShouldCallHandleSnapshotAndUpdateOrderbook() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode snapshotMessage = objectMapper.readTree(CoinbaseWebSocketClientEndpointTest.class.getClassLoader().getResource("ExampleSnapshot.json"));

        assertThrows(NullPointerException.class, (Executable) coinbaseWebSocketClientEndpoint.getOrderBook());

        coinbaseWebSocketClientEndpoint.handleCoinbaseJsonMessage(snapshotMessage);
        assertTrue(coinbaseWebSocketClientEndpoint.getOrderBook().getAsks().containsKey(new BigDecimal("36908.22")));
        assertTrue(coinbaseWebSocketClientEndpoint.getOrderBook().getAsks().get(new BigDecimal("36908.22")).equals(new BigDecimal("0.00529780")));
    }

    @Test
    public void testHandleCoinbaseMessageShouldCallHandleL2AndUpdateOrderbookForL2BuyUpdate() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode snapshotMessage = objectMapper.readTree(CoinbaseWebSocketClientEndpointTest.class.getClassLoader().getResource("ExampleSnapshot.json"));
        JsonNode l2Message = objectMapper.readTree(CoinbaseWebSocketClientEndpointTest.class.getClassLoader().getResource("ExampleL2BuyUpdate.json"));
        coinbaseWebSocketClientEndpoint.handleCoinbaseJsonMessage(snapshotMessage);
        coinbaseWebSocketClientEndpoint.handleL2Update(l2Message);
        // orderbook should no longer have a bid entry of 36944.35
        assertFalse(coinbaseWebSocketClientEndpoint.getOrderBook().getBids().containsKey(new BigDecimal("36944.35")));
    }

    @Test
    public void testHandleCoinbaseMessageShouldCallHandleL2AndUpdateOrderbookForL2SellUpdate() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode snapshotMessage = objectMapper.readTree(CoinbaseWebSocketClientEndpointTest.class.getClassLoader().getResource("ExampleSnapshot.json"));
        JsonNode l2Message = objectMapper.readTree(CoinbaseWebSocketClientEndpointTest.class.getClassLoader().getResource("ExampleL2SellUpdate.json"));
        coinbaseWebSocketClientEndpoint.handleCoinbaseJsonMessage(snapshotMessage);
        coinbaseWebSocketClientEndpoint.handleL2Update(l2Message);
        // orderbook should no longer have an ask entry of 36991.04
        assertFalse(coinbaseWebSocketClientEndpoint.getOrderBook().getAsks().containsKey(new BigDecimal("36991.04")));
    }

    @Test
    public void testClose() {
        coinbaseWebSocketClientEndpoint.setWebSocketClientEndpoint(mockWebSocketClientEndpoint);
        coinbaseWebSocketClientEndpoint.close();
        verify(mockWebSocketClientEndpoint).close();
    }
}
