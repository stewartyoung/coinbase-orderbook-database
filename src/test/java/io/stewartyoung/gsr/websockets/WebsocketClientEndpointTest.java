package io.stewartyoung.gsr.websockets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.websocket.DeploymentException;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class WebsocketClientEndpointTest {
    @Mock
    protected Session session;

    @Mock
    protected WebsocketClientEndpoint.MessageHandler messageHandler;

    private WebsocketClientEndpoint websocketClientEndpoint;

    @BeforeEach
    public void setup() throws URISyntaxException, DeploymentException, IOException {
        websocketClientEndpoint = new WebsocketClientEndpoint(new URI("wss://ws-feed.pro.coinbase.com/"), messageHandler);
    }

    @Test
    public void testOnOpen() {
        websocketClientEndpoint.onOpen(session);
        assertEquals(session, websocketClientEndpoint.userSession);
    }

}
