package net.yukulab.pointactivity.config;

public record ClientConfig(int comboContinueTimeMillis) {
    @SuppressWarnings("checkstyle:MagicNumber")
    public static ClientConfig getAsDefault() {
        return new ClientConfig(1000);
    }
}
