package io.stewartyoung.orderbook.message;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/*
Example:
{
    "product_ids":["BTC-USD"],
    "channels":["level2",
                "heartbeat",
                {"product_ids":["BTC-USD"],"name":"ticker"}
                ],
    "type":"subscribe"
}
*/

public class L2SubscribeMessageGenerator
{
    /**
     * <p>Generates a l2 subscribe message for any given instrument passed in through command line.</p>
     * <p>
     * <div>
     * Example:
     * <p>{</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"product_ids":[&lt;instrument&gt;],</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"channels":["level2",</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"heartbeat",</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{"product_ids":[&lt;instrument&gt;],"name":"ticker"}</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;],</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"type":"subscribe"</p>
     * <p>}</p>
     * </div>
     * @param instrument the instrument to subscribe to on Coinbase
     * @return a json string containing l2 subscribe message to send to Coinbase
     */
    public static String getL2SubscribeMessage(String instrument)
    {
        JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);
        ObjectNode message = new ObjectNode(jsonNodeFactory);

        ArrayNode product_ids1 = message.arrayNode();
        product_ids1.add(instrument);
        message.set("product_ids", product_ids1);

        ArrayNode channels = message.arrayNode();
        channels.add("level2");
        channels.add("heartbeat");
        ObjectNode productIdsAndName = new ObjectNode(jsonNodeFactory);
        ArrayNode product_ids2 = productIdsAndName.arrayNode();
        product_ids2.add(instrument);
        productIdsAndName.set("product_ids", product_ids2);
        TextNode ticker = message.textNode("ticker");
        productIdsAndName.set("name", ticker);
        channels.add(productIdsAndName);

        message.set("channels", channels);
        TextNode subscribe = message.textNode("subscribe");
        message.set("type", subscribe);

        return message.toString();
    }
}