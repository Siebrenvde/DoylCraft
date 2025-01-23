package dev.siebrenvde.doylcraft.preferences;

import dev.siebrenvde.configlib.libs.quilt.config.api.ReflectiveConfig;
import dev.siebrenvde.configlib.libs.quilt.config.api.annotations.SerializedNameConvention;
import dev.siebrenvde.configlib.libs.quilt.config.api.metadata.NamingSchemes;
import dev.siebrenvde.configlib.libs.quilt.config.api.values.TrackedValue;
import dev.siebrenvde.configlib.metadata.NoOptionSpacing;

@NoOptionSpacing
@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
public class PlayerPreferences extends ReflectiveConfig {

    public final PrefDurabilityPing durabilityPing = new PrefDurabilityPing();

    public static class PrefDurabilityPing extends Section {
        public final TrackedValue<Boolean> enabled = value(true);
        public final TrackedValue<Integer> percentage = value(10);
        public final TrackedValue<Integer> cooldown = value(60);

        public boolean enabled() { return enabled.getRealValue(); }
        public int percentage() { return percentage.getRealValue(); }
        public int cooldown() { return cooldown.getRealValue(); }
    }

}
