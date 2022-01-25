package io.stewartyoung.gcr.message;

import org.json.JSONArray;
import org.json.JSONObject;

public class MessageFactory
{
    public static String getL2SubscribeMessage(String instrument)
    {
        JSONObject subscribeMessage = new JSONObject();
        subscribeMessage.put("type", "subscribe");

        JSONArray products = buildProductsList(instrument);
        subscribeMessage.put("product_ids", products);
        subscribeMessage.put("channels", buildChannelsList(products));

        return subscribeMessage.toString();
    }

    private static JSONArray buildChannelsList(JSONArray products)
    {
        JSONArray channels = new JSONArray();
        channels.put("level2");
        channels.put("heartbeat");

        JSONObject channel = new JSONObject();
        channel.put("name", "ticker");
        channel.put("product_ids", products);
        channels.put(channel);
        return channels;
    }

    private static JSONArray buildProductsList(String instrument)
    {
        JSONArray products = new JSONArray();
        products.put(instrument);
        return products;
    }
}