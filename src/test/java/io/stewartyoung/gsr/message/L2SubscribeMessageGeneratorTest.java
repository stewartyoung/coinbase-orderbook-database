package io.stewartyoung.gsr.message;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

public class L2SubscribeMessageGeneratorTest {

    @Test
    public void testGetL2SubscribeMessage() throws IOException {
        String testInstrument = "ETH-USD";
        JsonMapper mapper = new JsonMapper();
        URL testJsonUrl = this.getClass().getClassLoader().getResource("EthUsdExampleL2SubscribeMessage.json");
        String expected = mapper.readTree(testJsonUrl).toString();

        L2SubscribeMessageGenerator l2SubscribeMessageGenerator = new L2SubscribeMessageGenerator();
        String actual = l2SubscribeMessageGenerator.getL2SubscribeMessage(testInstrument);
        Assertions.assertEquals(expected, actual);
    }
}
