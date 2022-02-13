package io.stewartyoung.orderbook.websockets;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.tyrus.client.ClientManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import javax.websocket.DeploymentException;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebSocketClientEndpointTest {
    @Mock
    protected Session session;

    @Mock
    protected WebSocketClientEndpoint.MessageHandler messageHandler;

    private WebSocketClientEndpoint websocketClientEndpoint;

    private ObjectMapper objectMapper = new ObjectMapper();
    private JsonNode message;
    private String messageString;

    @BeforeEach
    public void setup() throws URISyntaxException, DeploymentException, IOException {
        websocketClientEndpoint = new WebSocketClientEndpoint(new URI("wss://ws-feed.pro.coinbase.com/"), messageHandler);
        message = objectMapper.readTree(WebSocketClientEndpointTest.class.getClassLoader().getResource("ExampleL2BuyUpdate.json"));
        messageString = message.toString();
    }

    @Test
    public void testConnect() throws DeploymentException, IOException {
        websocketClientEndpoint.connect();
        assertTrue(websocketClientEndpoint.getWebSocketContainer() instanceof ClientManager);
    }

    @Test
    public void testOnOpen() {
        websocketClientEndpoint.onOpen(session);
        // assert sessions has been added as member variable of websocketClientEndpoint after onOpen
        assertEquals(session, websocketClientEndpoint.getUserSession());
    }

    @Test
    public void testOnMessage() throws IOException {
        websocketClientEndpoint.onOpen(session);
        websocketClientEndpoint.onMessage(messageString);
        // verify messageHandler.handleMessage() is called after onMessage
        verify(messageHandler).handleMessage(message);
    }

    @Test
    public void testOnClose() {
        websocketClientEndpoint.close();
        assertEquals(websocketClientEndpoint.getUserSession(), null);
    }

    @Test
    public void testOnError() {
        Logger websocketClientEndpointLogger = (Logger) LoggerFactory.getLogger(WebSocketClientEndpoint.class);
        websocketClientEndpointLogger.setLevel(Level.ERROR);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        websocketClientEndpointLogger.addAppender(listAppender);
        List<ILoggingEvent> logsList = listAppender.list;

        websocketClientEndpoint.onError(session, new Throwable());
        assertTrue(logsList.get(0).getMessage().contains("Error for the session"));
    }

    @Test
    public void testSendMessage() throws DeploymentException, IOException, URISyntaxException {
        WebSocketClientEndpoint testWebSocketClientEndpoint = new WebSocketClientEndpoint(new URI("wss://ws-feed.pro.coinbase.com/"), messageHandler);
        testWebSocketClientEndpoint.connect();
        testWebSocketClientEndpoint.sendMessage(messageString);
    }

    @Test
    public void testClose() throws IOException {
        websocketClientEndpoint.onOpen(session);
        assertEquals(session, websocketClientEndpoint.getUserSession());

        when(session.isOpen()).thenReturn(true);
        websocketClientEndpoint.close();
        verify(session).isOpen();
        verify(session).close();
    }

}
