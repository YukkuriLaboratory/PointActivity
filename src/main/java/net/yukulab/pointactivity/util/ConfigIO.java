package net.yukulab.pointactivity.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.yukulab.pointactivity.PointActivity;

import java.io.*;
import java.util.Optional;

public class ConfigIO {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "PointActivity.json");

    public static <T> void writeConfig(T config) {
        var json = gson.toJson(config);
        try (var writer = new FileWriter(configFile, false)) {
            writer.write(json);
        } catch (IOException e) {
            PointActivity.LOGGER.error("Failed to write config", e);
        }
    }

    public static <T> Optional<T> readConfig(Class<T> targetObject) {
        try (var reader = new FileReader(configFile)) {
            var config = gson.fromJson(reader, targetObject);
            return Optional.of(config);
        } catch (FileNotFoundException e) {
            PointActivity.LOGGER.warn("Config file not found. Load default data", e);
        } catch (IOException e) {
            PointActivity.LOGGER.error("Failed to load config", e);
        }
        return Optional.empty();
    }
}
