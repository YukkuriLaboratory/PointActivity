package net.yukulab.pointactivity.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE =
            new File(FabricLoader.getInstance().getConfigDir().toFile(), "PointActivity.json");

    public static <T> void writeConfig(T config) {
        var json = GSON.toJson(config);
        try (var writer = new FileWriter(CONFIG_FILE, false)) {
            writer.write(json);
        } catch (IOException e) {
            PointActivity.LOGGER.error("Failed to write config", e);
        }
    }

    public static <T> Optional<T> readConfig(Class<T> targetObject) {
        try (var reader = new FileReader(CONFIG_FILE)) {
            var config = GSON.fromJson(reader, targetObject);
            return Optional.of(config);
        } catch (FileNotFoundException e) {
            PointActivity.LOGGER.warn("Config file not found. Load default data", e);
        } catch (IOException e) {
            PointActivity.LOGGER.error("Failed to load config", e);
        }
        return Optional.empty();
    }
}
