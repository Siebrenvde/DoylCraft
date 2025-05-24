package dev.siebrenvde.doylcraft.config;

import dev.siebrenvde.configlib.ConfigLib;
import dev.siebrenvde.configlib.serialisers.toml.TomlSerialiser;
import dev.siebrenvde.doylcraft.DoylCraft;
import org.jspecify.annotations.NullMarked;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.SerializedNameConvention;
import org.quiltmc.config.api.metadata.NamingSchemes;
import org.quiltmc.config.api.values.TrackedValue;

import java.io.IOException;
import java.nio.file.Files;

@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
@NullMarked
public class Config extends ReflectiveConfig {

    @Comment("Whether the daylight cycle should be enabled")
    public final TrackedValue<Boolean> daylightCycle = value(true);

    public boolean daylightCycle() {
        return daylightCycle.getRealValue();
    }

    private static final Config INSTANCE = ConfigLib.toml(
        DoylCraft.instance().getDataPath(),
        "config",
        Config.class
    );

    public static Config config() {
        return INSTANCE;
    }

    public static void reload() {
        try {
            TomlSerialiser.INSTANCE.deserialize(
                INSTANCE,
                Files.newInputStream(DoylCraft.instance().getDataPath().resolve("config.toml"))
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to reload config", e);
        }
    }

}
