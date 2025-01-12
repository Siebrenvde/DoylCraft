package dev.siebrenvde.doylcraft.addons;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import dev.siebrenvde.doylcraft.DoylCraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static net.kyori.adventure.text.Component.text;

public class VoicechatAddon implements VoicechatPlugin {

    private static final String MODRINTH_URL = "https://modrinth.com/plugin/simple-voice-chat";
    private static final String CURSEFORGE_URL = "https://www.curseforge.com/minecraft/mc-mods/simple-voice-chat";

    private static String VOICECHAT_VERSION;

    private static VoicechatServerApi serverApi;

    @Override
    public String getPluginId() { return "doylcraft"; }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStart);
    }

    private void onServerStart(VoicechatServerStartedEvent event) {
        serverApi = event.getVoicechat();
        VOICECHAT_VERSION = Bukkit.getServer().getPluginManager().getPlugin("voicechat").getPluginMeta().getVersion();
    }

    /**
     * Checks whether a player has voicechat installed and sends them a message if not
     * @param player the player to check
     */
    public static void checkVoicechatInstalled(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                VoicechatConnection connection = serverApi.getConnectionOf(player.getUniqueId());
                if (connection != null && !connection.isInstalled()) {
                    player.sendMessage(
                        text(String.format("Simple Voice Chat (%s) is supported on this server", VOICECHAT_VERSION), NamedTextColor.GOLD)
                            .append(Component.newline())
                            .append(text("You can download it from "))
                            .append(
                                text("Modrinth")
                                    .decorate(TextDecoration.UNDERLINED)
                                    .clickEvent(ClickEvent.openUrl(MODRINTH_URL))
                                    .hoverEvent(HoverEvent.showText(text(MODRINTH_URL)))
                            )
                            .append(text(" or "))
                            .append(
                                text("CurseForge")
                                    .decorate(TextDecoration.UNDERLINED)
                                    .clickEvent(ClickEvent.openUrl(CURSEFORGE_URL))
                                    .hoverEvent(HoverEvent.showText(text(CURSEFORGE_URL)))
                            )
                    );
                }
            }
        }.runTaskLater(DoylCraft.getInstance(), 40);
    }

}
