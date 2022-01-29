package io.stewartyoung.gcr.websockets;

import com.fasterxml.jackson.databind.JsonNode;
import io.stewartyoung.gcr.api.CoinbaseMessageConverter;
import io.stewartyoung.gcr.api.OrderBookPrinter;
import io.stewartyoung.gcr.message.L2SubscribeMessageGenerator;
import io.stewartyoung.gcr.model.OrderBook;
import io.stewartyoung.gcr.model.OrderBookUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CoinbaseWebsocketClientEndpoint {
    private final Logger LOG = LoggerFactory.getLogger(CoinbaseWebsocketClientEndpoint.class);

    public final String coinbaseWebsocketUri = "wss://ws-feed.pro.coinbase.com/";

    private WebsocketClientEndpoint websocketClientEndpoint;
    private OrderBook orderBook;
    private final OrderBookPrinter orderBookPrinter = new OrderBookPrinter();

    public void subscribe(String instrument) {
        try {
            websocketClientEndpoint = new WebsocketClientEndpoint(new URI(coinbaseWebsocketUri), this::handleCoinbaseJsonMessage);

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

    public void handleCoinbaseJsonMessage(JsonNode jsonMessage) {
        String type = jsonMessage.get("type").asText();
        if (type == null) {
            LOG.debug("Skipping jsonMessage {}", jsonMessage);
        }
        switch (type) {
            // we only care about these two types of messages in our case
            // can handle more types if we need to here
            case "l2update":
                handleL2Update(jsonMessage);
                break;
            case "snapshot":
                handleSnapshot(jsonMessage);
                break;
        }
    }

    public void handleSnapshot(JsonNode snapshotJsonMessage) {
        orderBook = CoinbaseMessageConverter.convertSnapshot(snapshotJsonMessage);
        orderBookPrinter.print(orderBook);
    }

    public void handleL2Update(JsonNode l2JsonMessage) {
        OrderBookUpdate orderBookUpdate = CoinbaseMessageConverter.convertL2(l2JsonMessage);
        if (orderBook != null) {
            orderBook.l2UpdateOrderBook(orderBookUpdate);
            orderBookPrinter.print(orderBook);
        }
    }

    public void close() {websocketClientEndpoint.close();}
}
