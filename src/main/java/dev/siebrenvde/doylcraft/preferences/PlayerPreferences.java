package dev.siebrenvde.doylcraft.preferences;

import dev.siebrenvde.configlib.libs.quilt.config.api.ReflectiveConfig;
import dev.siebrenvde.configlib.libs.quilt.config.api.annotations.SerializedNameConvention;
import dev.siebrenvde.configlib.libs.quilt.config.api.metadata.NamingSchemes;
import dev.siebrenvde.configlib.libs.quilt.config.api.values.TrackedValue;
import dev.siebrenvde.configlib.metadata.NoOptionSpacing;

import java.time.ZoneId;

@NoOptionSpacing
@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
public class PlayerPreferences extends ReflectiveConfig {

    public final TrackedValue<String> timezone = value("UTC");
    public final TrackedValue<Boolean> voicechatReminder = value(true);

    public final PrefDurabilityPing durabilityPing = new PrefDurabilityPing();
    public final PrefPetDamageMessages petDamageMessages = new PrefPetDamageMessages();

    public static class PrefDurabilityPing extends Section {
        public final TrackedValue<Boolean> enabled = value(true);
        public final TrackedValue<Integer> percentage = value(10);
        public final TrackedValue<Integer> cooldown = value(60);

        public boolean enabled() { return enabled.getRealValue(); }
        public int percentage() { return percentage.getRealValue(); }
        public int cooldown() { return cooldown.getRealValue(); }
    }

    public static class PrefPetDamageMessages extends Section {
        public final TrackedValue<Boolean> broadcast = value(true);
        public final TrackedValue<Boolean> owner = value(true);
        public final TrackedValue<Boolean> attacker = value(true);

        public boolean broadcast() { return broadcast.getRealValue(); }
        public boolean owner() { return owner.getRealValue(); }
        public boolean attacker() { return attacker.getRealValue(); }
    }

    public ZoneId timezone() { return ZoneId.of(timezone.getRealValue()); }
    public boolean voicechatReminder() { return voicechatReminder.getRealValue(); }

}
