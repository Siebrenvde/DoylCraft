package dev.siebrenvde.doylcraft.addons;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import dev.siebrenvde.doylcraft.DoylCraft;
import dev.siebrenvde.doylcraft.player.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;

@NullMarked
public class VoicechatAddon implements VoicechatPlugin {

    private static final String MODRINTH_URL = "https://modrinth.com/plugin/simple-voice-chat";
    private static final String CURSEFORGE_URL = "https://www.curseforge.com/minecraft/mc-mods/simple-voice-chat";

    private static String VOICECHAT_VERSION = "";

    @Nullable private static VoicechatServerApi serverApi;

    @Override
    public String getPluginId() { return "doylcraft"; }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStart);
    }

    private void onServerStart(VoicechatServerStartedEvent event) {
        serverApi = event.getVoicechat();
        VOICECHAT_VERSION = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("voicechat")).getPluginMeta().getVersion();
    }

    /**
     * Checks whether a player has voicechat installed and sends them a message if not
     * @param player the player to check
     */
    public static void checkVoicechatInstalled(Player player) {
        if(!PlayerData.preferences(player).voicechatReminder()) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                VoicechatConnection connection = serverApi().getConnectionOf(player.getUniqueId());
                if (connection != null && !connection.isInstalled()) {
                    player.sendMessage(
                        text()
                            .append(text(String.format("Simple Voice Chat (%s) is supported on this server", VOICECHAT_VERSION)))
                            .appendNewline()
                            .append(text("You can download it from "))
                            .append(link("Modrinth", MODRINTH_URL))
                            .append(text(" or "))
                            .append(link("CurseForge", CURSEFORGE_URL))
                            .color(NamedTextColor.GOLD)
                    );
                }
            }
        }.runTaskLater(DoylCraft.instance(), 40);
    }

    private static Component link(String name, String url) {
        return text(name)
            .decorate(TextDecoration.UNDERLINED)
            .clickEvent(ClickEvent.openUrl(url))
            .hoverEvent(HoverEvent.showText(text(url)));
    }

    private static VoicechatServerApi serverApi() { return Objects.requireNonNull(serverApi); }

}
