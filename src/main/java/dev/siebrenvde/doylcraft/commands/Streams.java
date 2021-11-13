package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.utils.Requests;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Streams implements CommandExecutor {

    private Main main;
    private Requests requests;

    public Streams(Main main) {
        this.main = main;
        requests = new Requests();
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        new BukkitRunnable() {
            @Override
            public void run() {

                // TODO: Replace with online streamers, using offline for testing
                ArrayList<String> onlineStreamers = (ArrayList<String>) requests.get("https://api.siebrenvde.dev/doylcraft/streamers").get("offline_streamers");

                TextComponent text = Component.text("Online Streamers:");

                for(String streamer : onlineStreamers) {
                    text = text.append(Component.text("\n" + streamer).clickEvent(ClickEvent.openUrl("https://twitch.tv/" + streamer)));
                }

                sender.sendMessage(text);

                // TODO: Send sender message with clickable names leading to Twitch page
                // TODO: Add stream title and game
                // TODO: Send message in case no online streamers

            }
        }.runTaskAsynchronously(main);

        return true;
    }

}
