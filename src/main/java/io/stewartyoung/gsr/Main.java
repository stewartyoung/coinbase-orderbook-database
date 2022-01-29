package io.stewartyoung.gsr;

import io.stewartyoung.gsr.websockets.CoinbaseWebsocketClientEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Uncomment below two lines, import packages, and throw exceptions, if you'd like to store logs for debugging
//        PrintStream out = new PrintStream(new FileOutputStream("debugging.txt"));
//        System.setOut(out);

        if (args.length > 1) {
            throw new IllegalArgumentException("Please pass one cryptocurrency pair as an argument. You passed " + String.join(", ", args) + " of size " + args.length);
        }

        String instrument = args[0];

        LOG.info("Starting Coinbase order book application for: {}", instrument);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(countDownLatch::countDown));

        CoinbaseWebsocketClientEndpoint coinbaseWebsocketClientEndpoint = new CoinbaseWebsocketClientEndpoint();
        coinbaseWebsocketClientEndpoint.subscribe(args[0]);

        try {
            countDownLatch.await();
            LOG.info("Exiting Coinbase order book application");
            coinbaseWebsocketClientEndpoint.close();
        } catch (InterruptedException ignored) {

        }
    }
}
