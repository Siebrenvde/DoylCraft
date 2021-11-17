package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.utils.Requests;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
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
                JSONArray onlineStreamers = requests.get("https://api.siebrenvde.dev/doylcraft/streamers").getJSONArray("online_streams");

                if(onlineStreamers.length() == 0) {
                    sender.sendMessage("No online streamers.");
                    return;
                }

                TextComponent text = Component.text("Online Streamers:");

                for(int i = 0; i < onlineStreamers.length(); i++) {

                    JSONObject streamer = onlineStreamers.getJSONObject(i);
                    text = text.append(Component.text("\n" + streamer.getString("user_name")).clickEvent(ClickEvent.openUrl("https://twitch.tv/" + streamer.getString("user_login"))));

                }

                sender.sendMessage(text);

                // TODO:
                //  Send sender message with clickable names leading to Twitch page
                //  Add stream title and game
                //  Send message in case no online streamers

            }
        }.runTaskAsynchronously(main);

        return true;
    }

}
