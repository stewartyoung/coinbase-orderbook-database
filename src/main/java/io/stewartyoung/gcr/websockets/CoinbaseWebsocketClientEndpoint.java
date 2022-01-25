package io.stewartyoung.gcr.websockets;

import io.stewartyoung.gcr.message.L2SubscribeMessageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CoinbaseWebsocketClientEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(CoinbaseWebsocketClientEndpoint.class);

    public static final String coinbaseWebsocketUri = "wss://ws-feed.pro.coinbase.com/";

    WebsocketClientEndpoint websocketClientEndpoint;

    public void subscribe(String instrument) {
        try {
            WebsocketClientEndpoint.MessageHandler coinbaseMessageHandler = LOG::info;
            websocketClientEndpoint = new WebsocketClientEndpoint(new URI(CoinbaseWebsocketClientEndpoint.coinbaseWebsocketUri), coinbaseMessageHandler);

            websocketClientEndpoint.connect();

            LOG.info("Subscribing to instrument: {}", instrument);
            String l2SubsribeMessage = L2SubscribeMessageGenerator.getL2SubscribeMessage(instrument);
            LOG.info("Subscribe message: {}", l2SubsribeMessage);
            websocketClientEndpoint.sendMessage(l2SubsribeMessage);

        } catch (DeploymentException | IOException e) {
            LOG.error("Error in endpoint configuration", e.getMessage());
        } catch (URISyntaxException e) {
            LOG.error("Invalid URI provided: {}", e.getMessage());
        }
    }

    public void close() {websocketClientEndpoint.close();}
}
