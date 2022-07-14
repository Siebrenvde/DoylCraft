package dev.siebrenvde.doylcraft.handlers;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class TimeHandler {

    private HashMap<Player, Long> loginTimes;

    public TimeHandler() {
        loginTimes = new HashMap<>();
    }

    public long getLoginTime(Player player) { return loginTimes.get(player); }

    public long getOnlineTime(Player player) {
        long loginTime = loginTimes.get(player);
        long currentTime = System.currentTimeMillis();
        return currentTime - loginTime;
    }

    public void addLoginTime(Player player) {
        loginTimes.put(player, System.currentTimeMillis());
    }

    public void removeLoginTime(Player player) {
        loginTimes.remove(player);
    }

    public void setLoginTime(Player player, Long time) {
        loginTimes.put(player, time);
    }

    public static String formatTime(long secondsL) {

        // https://stackoverflow.com/questions/19667473/

        long minutesL = secondsL / 60;
        long hoursL = minutesL / 60;
        long days = hoursL / 24;

        long seconds = secondsL % 60;
        long minutes = minutesL % 60;
        long hours = hoursL % 24;

        String s = "";

        if(days == 1) {
            s += "1 day, ";
        } else if(days > 1) {
            s += days + " days, ";
        }

        if(hours == 0) {
            if(s.contains("day") || s.contains("days")) {
                s += "0 hours, ";
            }
        } else if(hours == 1)  {
            s += "1 hour, ";
        } else if(hours > 1) {
            s += hours + " hours, ";
        }

        if(minutes == 0) {
            if(s.contains("hour") || s.contains("hours")) {
                s += "0 minutes, ";
            }
        } else if(minutes == 1)  {
            s += "1 minute, ";
        } else if(minutes > 1) {
            s += minutes + " minutes, ";
        }

        if(seconds == 0) {
            if(s.contains("minute") || s.contains("minutes")) {
                s += "0 seconds";
            }
        } else if(seconds == 1)  {
            s += "1 second";
        } else if(seconds > 1) {
            s += seconds + " seconds";
        }

        return s;
    }

}
