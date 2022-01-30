package io.stewartyoung.gsr.websockets;

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
    public void testConnect() throws DeploymentException, IOException {
        websocketClientEndpoint.connect();
        assertTrue(websocketClientEndpoint.getWebsocketContainer() instanceof ClientManager);
    }

    @Test
    public void testOnOpen() {
        websocketClientEndpoint.onOpen(session);
        // assert sessions has been added as member variable of websocketClientEndpoint after onOpen
        assertEquals(session, websocketClientEndpoint.getUserSession());
    }

    @Test
    public void testOnMessage() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode message = objectMapper.readTree(WebsocketClientEndpointTest.class.getClassLoader().getResource("ExampleL2BuyUpdate.json"));
        String messageString = message.toString();
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
        Logger websocketClientEndpointLogger = (Logger) LoggerFactory.getLogger(WebsocketClientEndpoint.class);
        websocketClientEndpointLogger.setLevel(Level.ERROR);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        websocketClientEndpointLogger.addAppender(listAppender);
        List<ILoggingEvent> logsList = listAppender.list;

        websocketClientEndpoint.onError(session, new Throwable());
        assertTrue(logsList.get(0).getMessage().contains("Error for the session"));
    }

}
