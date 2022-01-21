package io.stewartyoung.gcr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            logger.info("Starting Coinbase orderbook application");

            String coinbaseWebsocketEndpoint = "wss://ws-feed.exchange.coinbase.com/";

            // open websocket
            WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI(coinbaseWebsocketEndpoint));

            // add listener
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            // send message to websocket
            clientEndPoint.sendMessage("{'event':'addChannel','channel':'ok_btccny_ticker'}");

            // wait 5 seconds for messages from websocket
            Thread.sleep(5000);

            logger.info("Connecting to coinbase websocket feed {}", coinbaseWebsocketEndpoint);
        } catch (Throwable t) {
            logger.info("Exiting Coinbase orderbook application: {}", t.getMessage());
        }
    }
}
