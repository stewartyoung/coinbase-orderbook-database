package io.stewartyoung.gsr.websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;


@ClientEndpoint
public class WebsocketClientEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(WebsocketClientEndpoint.class);

    private final URI endpointUri;
    private Session userSession = null;
    private final MessageHandler messageHandler;

    public WebsocketClientEndpoint(URI endpointUri, MessageHandler messageHandler) throws DeploymentException, IOException {
        this.endpointUri = endpointUri;
        this.messageHandler = messageHandler;
    }

    public void connect() throws DeploymentException, IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, endpointUri);
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonMessage = objectMapper.readTree(message);
        messageHandler.handleMessage(jsonMessage);
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        LOG.info("Closing websocket");
        this.userSession = null;
    }

    @OnError
    public void onError(Session session, Throwable throwable)
    {
       LOG.error("Error for the session {}: \n{}", session, throwable.getMessage());
    }


    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        userSession.getAsyncRemote().sendText(message);
    }

    public void close() {
        if (userSession != null && userSession.isOpen()) {
            try {
                userSession.close();
            } catch (IOException e) {
                LOG.error("Failed to close session gracefully: {}", e);
            }
        }
    }

    public interface MessageHandler {
        void handleMessage(JsonNode message);
    }

}
