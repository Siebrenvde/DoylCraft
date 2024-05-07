package dev.siebrenvde.doylcraft.handlers;

import org.bukkit.entity.Player;

public class TimeHandler {

    private final MemoryHandler memoryHandler;

    public TimeHandler(MemoryHandler memoryHandler) {
        this.memoryHandler = memoryHandler;
    }

    public long getOnlineTime(Player player) {
        long loginTime = memoryHandler.getLoginTime(player);
        long currentTime = System.currentTimeMillis();
        return currentTime - loginTime;
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
