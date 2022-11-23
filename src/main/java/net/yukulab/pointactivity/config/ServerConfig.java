package net.yukulab.pointactivity.config;

import com.google.gson.annotations.JsonAdapter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;

@JsonAdapter(RecordTypeAdapterFactory.class)
public record ServerConfig(int moveHorizontalPointPer, int moveVerticalPointPer) {
    public static ServerConfig getAsDefault() {
        return new ServerConfig(100, 250);
    }
}
