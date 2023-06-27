package net.yukulab.pointactivity.config;

import com.google.gson.annotations.JsonAdapter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;

@JsonAdapter(RecordTypeAdapterFactory.class)
public record ServerConfig(
        int returnCountSec,
        int moveHorizontalPointPer,
        int moveVerticalPointPer,
        int craftPoint,
        int swingHandPoint,
        int attackPoint,
        int bowPointPer,
        int foodPointPer,
        int potionPointPer,
        int manaitaHandPoint,
        int manaitaBlockPoint,
        int deathPenalty
) {
    public static ServerConfig getAsDefault() {
        return new ServerConfig(10, 100, 250, 10, 1, 3, 10, 5, 5, 10, 10, 100);
    }
}
