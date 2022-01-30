package io.stewartyoung.gsr.websockets;

import com.fasterxml.jackson.databind.JsonNode;
import io.stewartyoung.gsr.api.CoinbaseMessageConverter;
import io.stewartyoung.gsr.api.OrderBookPrinter;
import io.stewartyoung.gsr.message.L2SubscribeMessageGenerator;
import io.stewartyoung.gsr.model.OrderBook;
import io.stewartyoung.gsr.model.OrderBookUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CoinbaseWebsocketClientEndpoint {
    private final Logger LOG = LoggerFactory.getLogger(CoinbaseWebsocketClientEndpoint.class);

    private final String coinbaseWebsocketUri = "wss://ws-feed.pro.coinbase.com/";
    @Setter(AccessLevel.PACKAGE)
    private WebsocketClientEndpoint websocketClientEndpoint;
    private CoinbaseMessageConverter coinbaseMessageConverter;
    @Getter(AccessLevel.PACKAGE)
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
        if (this.coinbaseMessageConverter == null) {
            this.coinbaseMessageConverter = new CoinbaseMessageConverter();
        }
        String type = jsonMessage.get("type").asText();
        if (type == null) {
            LOG.debug("Skipping jsonMessage {}", jsonMessage);
        }
        switch (type) {
            // we only care about these two types of messages in our case
            // can handle more types in the future if we need to here
            case "l2update":
                handleL2Update(jsonMessage);
                break;
            case "snapshot":
                handleSnapshot(jsonMessage);
                break;
        }
    }

    public void handleSnapshot(JsonNode snapshotJsonMessage) {
        orderBook = coinbaseMessageConverter.convertSnapshot(snapshotJsonMessage);
        orderBookPrinter.print(orderBook);
    }

    public void handleL2Update(JsonNode l2JsonMessage) {
        OrderBookUpdate orderBookUpdate = coinbaseMessageConverter.convertL2(l2JsonMessage);
        if (orderBook != null) {
            orderBook.l2UpdateOrderBook(orderBookUpdate);
            orderBookPrinter.print(orderBook);
        }
    }

    public void close() {websocketClientEndpoint.close();}
}
