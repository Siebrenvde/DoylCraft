package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.utils.Colours;
import dev.siebrenvde.doylcraft.utils.Requests;
import dev.siebrenvde.doylcraft.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONObject;

public class Streams implements CommandExecutor {

    private Main main;
    private Requests requests;

    public Streams(Main main) {
        this.main = main;
        requests = main.getRequests();
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        new BukkitRunnable() {
            @Override
            public void run() {

                // TEMP: Using temporary streamers in API for testing
                JSONArray onlineStreams = requests.get("https://api.siebrenvde.dev/doylcraft/streamers").getJSONArray("online_streams");

                if(onlineStreams.length() == 0) {
                    sender.sendMessage("No online streamers.");
                    return;
                }

                TextComponent text = Component.text("Online Streamers:").color(Colours.twitch);

                for(int i = 0; i < onlineStreams.length(); i++) {

                    JSONObject stream = onlineStreams.getJSONObject(i);
                    TextComponent streamText = Component.text("\n" + stream.getString("user_name")).color(TextColor.fromHexString("#ffffff"));

                    streamText = streamText.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Component.text("Title: ").color(Colours.twitch)
                            .append(Component.text(stream.getString("title")).color(TextColor.fromHexString("#ffffff")))
                            .append(Component.text("\nGame: ").color(Colours.twitch))
                            .append(Component.text(stream.getString("game_name")).color(TextColor.fromHexString("#ffffff")))
                            .append(Component.text("\nDuration: ").color(Colours.twitch))
                            .append(Component.text(Utils.getStreamDuration(stream.getString("started_at"))).color(TextColor.fromHexString("#ffffff")))
                    ));

                    streamText = streamText.clickEvent(ClickEvent.openUrl("https://twitch.tv/" + stream.getString("user_name")));

                    text = text.append(streamText);

                }

                sender.sendMessage(text);

            }
        }.runTaskAsynchronously(main);

        return true;
    }

}
