package net.yukulab.pointactivity.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.yukulab.pointactivity.PointActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class ConfigIO {
    protected ConfigIO() {
        throw new UnsupportedOperationException("Do not create this class instance");
    }

    private static final Gson GSON =
            new GsonBuilder().registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
                    .setPrettyPrinting()
                    .create();

    public static <T> void writeConfig(T config) {
        var json = GSON.toJson(config);
        try (var writer = new FileWriter(getConfigFile(config.getClass()), false)) {
            writer.write(json);
        } catch (IOException e) {
            PointActivity.LOGGER.error("Failed to write config", e);
        }
    }

    public static <T> Optional<T> readConfig(Class<T> targetObject) {
        try (var reader = new FileReader(getConfigFile(targetObject))) {
            var config = GSON.fromJson(reader, targetObject);
            return Optional.of(config);
        } catch (FileNotFoundException ignored) {
            PointActivity.LOGGER.warn("Config file not found. Load default data");
        } catch (IOException e) {
            PointActivity.LOGGER.error("Failed to load config", e);
        }
        return Optional.empty();
    }

    private static <T> File getConfigFile(Class<T> targetObject) {
        var builder = new StringBuilder().append("PointActivity");
        if (targetObject.equals(ClientConfig.class)) {
            builder.append("_client");
        } else if (targetObject.equals(ServerConfig.class)) {
            builder.append("_server");
        } else {
            builder.append("_unknown");
        }
        builder.append(".json");
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), builder.toString());
    }
}
