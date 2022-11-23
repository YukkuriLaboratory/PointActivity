package net.yukulab.pointactivity.config;

import com.google.gson.annotations.JsonAdapter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;

@JsonAdapter(RecordTypeAdapterFactory.class)
public record ClientConfig(int comboContinueTimeMillis, int pointAnimationTimeMillis) {
    @SuppressWarnings("checkstyle:MagicNumber")
    public static ClientConfig getAsDefault() {
        return new ClientConfig(1000, 600);
    }
}
