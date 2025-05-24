package dev.siebrenvde.doylcraft.handlers;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import static dev.siebrenvde.doylcraft.config.Config.config;

@NullMarked
public class DaylightCycleHandler {

    public static void handlePlayerJoin(Player player) {
        if (!checkServerEmpty(player)) return;
        World world = Bukkit.getWorlds().getFirst();
        if (config().daylightCycle() && Boolean.FALSE.equals(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE))) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        }
    }

    public static void handlePlayerQuit(Player player) {
        if (!checkServerEmpty(player)) return;
        World world = Bukkit.getWorlds().getFirst();
        if (Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE))) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean checkServerEmpty(Player player) {
        return Bukkit.getOnlinePlayers().stream()
            .filter(p -> !(p.equals(player)))
            .toList()
            .isEmpty();
    }

}
