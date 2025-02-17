package org.fun.tops.Handler;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;

public class PlaceholderHandler {
    public static String parsePlaceholders(String text, String player) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(player), text);
        }
        return text;
    }
}