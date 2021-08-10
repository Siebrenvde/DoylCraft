package dev.siebrenvde.doylcraft.handlers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardHandler {

    private Scoreboard board;
    private Objective obj;

    public ScoreboardHandler() {
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        obj = board.registerNewObjective("deaths", "", (Component) null);
        obj.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }

    public void showScoreboard(Player player) {
        player.setScoreboard(board);
    }

    public void updatePlayer(Player player) {
        Score score = obj.getScore(player.getName());
        score.setScore(player.getStatistic(Statistic.DEATHS));
    }

}
