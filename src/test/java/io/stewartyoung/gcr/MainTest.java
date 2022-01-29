package io.stewartyoung.gcr;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MainTest {

    private String[] testArgs = {"BTC-USD"};
    private String[] invalidTestArgs = {"BTC-USD", "ETH-USD"};

    private static final Logger LOG = LoggerFactory.getLogger(MainTest.class);

    private PrintStream standardOut = System.out;
    private ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

//    @BeforeEach
//    public void setUp() {
//        System.setOut(new PrintStream(outputStreamCaptor));
//    }
//
//    @AfterEach
//    public void tearDown() {
//        System.setOut(standardOut);
//    }

    @Test
    public void testMainWithArgs() {
        long start = System.currentTimeMillis();
        LOG.info(String.valueOf(start));
        while (System.currentTimeMillis() - start < 5000) {
            LOG.info(String.valueOf(System.currentTimeMillis()));
            Main.main(testArgs);
        }
        assert outputStreamCaptor.toString().contains("ZZZZStarting Coinbase order book application for: " + testArgs[0]);

//        Thread timeoutThread = new Thread(() -> {
//            try {
//                Thread.sleep(50000);
//                assert outputStreamCaptor.toString().contains("ZZZZStarting Coinbase order book application for: " + testArgs[0]);
//            } catch (InterruptedException interruptedException) {
//                interruptedException.printStackTrace();
//            }
//        });
//        timeoutThread.start();
    }

    @Test
    public void testMainWithInvalidArgs() {
//        Exception exception = assertThrows(IllegalArgumentException.class, () ->
//            Main.main(invalidTestArgs)
//        );
//        LOG.info(String.valueOf(exception.getMessage().equals("Please pass one cryptocurrency pair as an argument. You passed " + invalidTestArgs.toString() + " of size " + invalidTestArgs.length)));
//        assertTrue(exception.getMessage().equals("Please pass one cryptocurrency pair as an argument. You passed " + invalidTestArgs.toString() + " of size " + invalidTestArgs.length));
    }

}
