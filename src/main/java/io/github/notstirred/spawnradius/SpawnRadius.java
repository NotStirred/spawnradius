package io.github.notstirred.spawnradius;

import com.google.gson.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpawnRadius implements ModInitializer {
    public static final String MOD_ID = "spawnradius";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final int DEFAULT_RADIUS = 11; // default
    public static int RADIUS = DEFAULT_RADIUS;

    @Override
    public void onInitialize() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("spawnradius.json");

        if (!createDefaultConfig(configPath)) {
            return;
        }

        try {
            JsonElement parsed = JsonParser.parseReader(new FileReader(configPath.toFile()));
            RADIUS = parsed.getAsJsonObject().get("radius").getAsInt();
        } catch (Throwable t) {
            LOGGER.error(String.format("Failed to set config values from file: %s", configPath), t);
        }
    }

    private static boolean createDefaultConfig(Path configPath) {
        if (Files.exists(configPath)) {
            return true;
        }

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("radius", DEFAULT_RADIUS);

            try (FileOutputStream fileOutputStream = new FileOutputStream(configPath.toFile())) {
                fileOutputStream.write(gson.toJson(jsonObject).getBytes());
            }
        } catch (Throwable t) {
            LOGGER.error(String.format("Failed to create default config file: %s", configPath), t);
        }
        return false;
    }
}