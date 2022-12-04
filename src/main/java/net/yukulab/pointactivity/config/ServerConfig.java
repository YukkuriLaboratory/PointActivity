package net.yukulab.pointactivity.config;

import com.google.gson.annotations.JsonAdapter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;

@JsonAdapter(RecordTypeAdapterFactory.class)
public record ServerConfig(
        int moveHorizontalPointPer,
        int moveVerticalPointPer,
        int craftPoint,
        int swingHandPoint,
        int attackPoint,
        int bowPointPer,
        int foodPointPer,
        int potionPointPer
) {
    public static ServerConfig getAsDefault() {
        return new ServerConfig(100, 250, 10, 1, 3, 10, 5, 5);
    }
}
