package io.stewartyoung.gcr;

import io.stewartyoung.gcr.websockets.CoinbaseWebsocketClientEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOG.info("Starting Coinbase orderbook application");

        CountDownLatch countDownLatch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(countDownLatch::countDown));

        CoinbaseWebsocketClientEndpoint coinbaseWebsocketClientEndpoint = new CoinbaseWebsocketClientEndpoint();
        coinbaseWebsocketClientEndpoint.subscribe("BTC-USD");

        try {
            countDownLatch.await();
            coinbaseWebsocketClientEndpoint.close();
        } catch (InterruptedException ignored) {

        }
        LOG.info("Exiting Coinbase orderbook application");

    }
}
