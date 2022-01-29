package io.stewartyoung.gsr.websockets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.stewartyoung.gsr.api.CoinbaseMessageConverter;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class CoinbaseWebsocketClientEndpointTest {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(CoinbaseWebsocketClientEndpointTest.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSubscribe() {
        WebsocketClientEndpoint websocketClientEndpoint = mock(WebsocketClientEndpoint.class);
        CoinbaseWebsocketClientEndpoint coinbaseWebsocketClientEndpoint = new CoinbaseWebsocketClientEndpoint();
        coinbaseWebsocketClientEndpoint.subscribe("BTC-USD");
        // when subscribing, should create one instance of WebsocketClientEndpoint
        verify(websocketClientEndpoint, times(1));
    }

    @Test
    public void testHandleCoinbaseL2JsonMessage() throws IOException {
        CoinbaseWebsocketClientEndpoint coinbaseWebsocketClientEndpoint = new CoinbaseWebsocketClientEndpoint();
        JsonNode testL2JsonMessage = objectMapper.readTree(this.getClass().getClassLoader().getResource("ExampleL2BuyUpdate.json"));
        coinbaseWebsocketClientEndpoint.handleCoinbaseJsonMessage(testL2JsonMessage);
        CoinbaseMessageConverter coinbaseMessageConverter = mock(CoinbaseMessageConverter.class);
        // when handling an l2 update json message, CoinbaseMessageConverter.convertL2() should be called once
        verify(coinbaseMessageConverter, times(1)).convertL2(testL2JsonMessage);
    }

    @Test
    public void testHandleCoinbaseSnapshotJsonMessage() throws IOException {
        CoinbaseWebsocketClientEndpoint coinbaseWebsocketClientEndpoint = new CoinbaseWebsocketClientEndpoint();
        JsonNode testSnapshotMessage = objectMapper.readTree(this.getClass().getClassLoader().getResource("ExampleSnapshot.json"));
        coinbaseWebsocketClientEndpoint.handleCoinbaseJsonMessage(testSnapshotMessage);
        CoinbaseMessageConverter coinbaseMessageConverter = mock(CoinbaseMessageConverter.class);
        // when handling a snapshot json message, CoinbaseMessageConverter.convertSnapshot() should be called once
        verify(coinbaseMessageConverter, times(1)).convertSnapshot(testSnapshotMessage);
    }
}
