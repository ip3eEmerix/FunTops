package org.fun.tops;

import org.bukkit.plugin.java.JavaPlugin;

public class LoggerUtil {
    private final JavaPlugin plugin;

    public LoggerUtil(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void log(String message) {
        plugin.getLogger().info("[FunTops] " + message);
    }

    public void warn(String message) {
        plugin.getLogger().warning("[FunTops] " + message);
    }

    public void error(String message) {
        plugin.getLogger().severe("[FunTops] " + message);
    }
}