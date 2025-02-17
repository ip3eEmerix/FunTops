package org.fun.tops;


import org.bukkit.plugin.java.JavaPlugin;
import org.fun.tops.Handler.TopCommandHandler;

public class FunTops extends JavaPlugin {
    private Messages messages;
    private LoggerUtil logger;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        messages = new Messages(this);
        logger = new LoggerUtil(this);
        new UpdateManager(this);
        getCommand("ft").setExecutor(new TopCommandHandler(this));
        logger.log("Плагин успешно запущен!");
    }

    @Override
    public void onDisable() {
        logger.log("Плагин выключен.");
    }
}