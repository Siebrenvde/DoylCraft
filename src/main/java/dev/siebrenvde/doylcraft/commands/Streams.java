package dev.siebrenvde.doylcraft.commands;

import dev.siebrenvde.doylcraft.Main;
import dev.siebrenvde.doylcraft.utils.Requests;
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

                // TODO: Send sender message with clickable names leading to Twitch page
                // TODO: Send message in case no online streamers

            }
        }.runTaskAsynchronously(main);

        return true;
    }

}
