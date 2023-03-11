package io.github.notstirred.spawnradius.forge;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SpawnRadius.MODID)
public class SpawnRadius
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "spawnradius";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final int DEFAULT_RADIUS = 11; // default
    public static int RADIUS = DEFAULT_RADIUS;

    public SpawnRadius() {
        MinecraftForge.EVENT_BUS.register(this);

        // ServerStartingEvent is too late for this
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("spawnradius.json");

        if (!createDefaultConfig(configPath)) {
            return;
        }

        try {
            JsonElement parsed = JsonParser.parseReader(new FileReader(configPath.toFile()));
            RADIUS = Math.max(0, parsed.getAsJsonObject().get("radius").getAsInt());
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
