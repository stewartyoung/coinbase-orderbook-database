package io.stewartyoung.gcr.websockets;

import com.fasterxml.jackson.databind.JsonNode;
import io.stewartyoung.gcr.api.CoinbaseMessageConverter;
import io.stewartyoung.gcr.api.OrderbookPrinter;
import io.stewartyoung.gcr.message.L2SubscribeMessageGenerator;
import io.stewartyoung.gcr.model.Orderbook;
import io.stewartyoung.gcr.model.OrderbookUpdate;
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
    private Orderbook orderbook;
    private final OrderbookPrinter orderbookPrinter = new OrderbookPrinter();

    public void subscribe(String instrument) {
        try {
//            WebsocketClientEndpoint.MessageHandler coinbaseMessageHandler = LOG::info;
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
//        if (type.equals("l2update")) {
//            LOG.info("L2 update: {}", jsonMessage);
//        }
//        LOG.info(String.valueOf(jsonMessage));
        switch (type) {
            case "l2update":
                handleL2Update(jsonMessage);
                break;
//            case "heartbeat":
//                handleHeartbeat(jsonMessage);
            case "snapshot":
                handleSnapshot(jsonMessage);
                break;
//            case "ticker":
//                handleTicker(jsonMessage);
        }
    }

    public void handleSnapshot(JsonNode snapshotJsonMessage) {
        orderbook = CoinbaseMessageConverter.convertSnapshot(snapshotJsonMessage);
        orderbookPrinter.print(orderbook);
    }

    public void handleL2Update(JsonNode l2JsonMessage) {
        OrderbookUpdate orderbookUpdate = CoinbaseMessageConverter.convertL2(l2JsonMessage);
        if (orderbook != null) {
            orderbook.l2UpdateOrderBook(orderbookUpdate);
            orderbookPrinter.print(orderbook);
        }
    }

    public void close() {websocketClientEndpoint.close();}
}
