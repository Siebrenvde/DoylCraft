package dev.siebrenvde.doylcraft.handlers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;

public class ScoreboardHandler {

    private Scoreboard board;
    private Objective obj;
    private HashMap<Player, Integer> deaths;

    public ScoreboardHandler() {
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        obj = board.registerNewObjective("deaths", "", (Component) null);
        obj.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        deaths = new HashMap<>();
    }

    public void showScoreboard(Player player) {
        player.setScoreboard(board);
    }

    public void initPlayer(Player player) {
        int d = player.getStatistic(Statistic.DEATHS);
        updateScoreboard(player, d);
        deaths.put(player, d);
    }

    public void removePlayer(Player player) {
        deaths.remove(player);
    }

    public void updatePlayer(Player player) {
        int d = deaths.get(player) + 1;
        deaths.put(player, d);
        updateScoreboard(player, d);
    }

    private void updateScoreboard(Player player, int d) {
        Score score = obj.getScore(player.getName());
        score.setScore(d);
        for(Player p : Bukkit.getOnlinePlayers()) {
            showScoreboard(p);
        }
    }

}
