import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.stewartyoung.orderbook.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainTest {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MainTest.class);
    private String[] testArgs = {"BTC-USD"};
    private String[] invalidTestArgs = {"BTC-USD", "ETH-USD"};

    @Test
    public void testMainWithValidArgs() throws InterruptedException, ExecutionException {
        Logger mainLogger = (Logger) LoggerFactory.getLogger(Main.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        mainLogger.addAppender(listAppender);
        List<ILoggingEvent> logsList = listAppender.list;

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Runnable runnableTask = () -> Main.main(testArgs);

        Future future = executorService.submit(runnableTask);
        try {
            LOG.info("Starting testing of Main.main() with future");
            LOG.info(future.get(2, TimeUnit.SECONDS).toString()); // throws TimeoutException after 2 secs of executing main
            // Main should not finish before 2 secs, websocket feed lasts until interrupted
            LOG.info("Finished executing future");
        } catch (TimeoutException e) {
            future.cancel(true);
            LOG.info("Terminated Main.main() future");
            boolean check = logsList.get(0).getFormattedMessage().contains("Starting Coinbase order book application for: " + testArgs[0]);
            LOG.info("First line of logs: \"Starting Coinbase order book application for: " + testArgs[0] + "\" is called? " + check);
            Assertions.assertTrue(logsList.get(0).getFormattedMessage().contains("Starting Coinbase order book application for: " + testArgs[0]));
        }
    }

    @Test
    public void testMainWithInvalidArgs() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Main.main(invalidTestArgs);
        });

        Assertions.assertTrue(thrown.getMessage().equals("Please pass one cryptocurrency pair as an argument. You passed " + String.join(", ", invalidTestArgs) + " of size " + invalidTestArgs.length));
    }
}
