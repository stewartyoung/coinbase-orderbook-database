//import ch.qos.logback.classic.Logger;
//import ch.qos.logback.classic.spi.ILoggingEvent;
//import ch.qos.logback.core.read.ListAppender;
//import io.stewartyoung.gsr.Main;
//import org.junit.jupiter.api.Test;
//import org.slf4j.LoggerFactory;
//
//public class MainTest {
//
//    private String[] testArgs = {"BTC-USD"};
//    private String[] invalidTestArgs = {"BTC-USD", "ETH-USD"};
//
//    @Test
//    public void testMainWithValidArgs() {
//        Logger mainLogger = (Logger) LoggerFactory.getLogger(Main.class);
//        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
//        listAppender.start();
//
//        mainLogger.addAppender(listAppender);
//
//    }
//
//    @Test
//    public void testMainWithInvalidArgs() {
//
//    }
//}
