package dev.siebrenvde.doylcraft.handlers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardHandler {

    private final Scoreboard board;
    private final Objective objective;

    public ScoreboardHandler() {
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = board.registerNewObjective("deaths", Criteria.DEATH_COUNT, (Component) null);
        objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }

    /**
     * Set the player's death count on the scoreboard and display the scoreboard to them
     * @param player the player
     */
    public void initPlayer(Player player) {
        Score score = objective.getScore(player.getName());
        score.setScore(player.getStatistic(Statistic.DEATHS));
        player.setScoreboard(board);
    }

}
