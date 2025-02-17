package org.fun.tops;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class Messages {
    private final FunTops plugin;
    private FileConfiguration messagesConfig;

    public Messages(FunTops plugin) {
        this.plugin = plugin;
        plugin.saveResource("messages.yml", false);
        reloadMessages();
    }

    public void reloadMessages() {
        messagesConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
    }

    public String getHelpMessage() {
        return format(messagesConfig.getString("messages.help"));
    }

    public String getReloadMessage() {
        return format(messagesConfig.getString("messages.reload"));
    }

    public String getNoPermissionMessage() {
        return format(messagesConfig.getString("messages.no_permission"));
    }

    public String getUnknownCommandMessage() {
        return format(messagesConfig.getString("messages.unknown_command"));
    }

    public String getInsufficientArgumentsMessage() {
        return format(messagesConfig.getString("messages.insufficient_arguments"));
    }

    public String getSpecifyTopMessage() {
        return format(messagesConfig.getString("messages.specify_top"));
    }

    public String getUnknownActionMessage() {
        return format(messagesConfig.getString("messages.unknown_action"));
    }

    public String getTopNotFoundMessage(String topName) {
        return format(messagesConfig.getString("messages.top_not_found")).replace("%top%", topName);
    }

    public String getInvalidTopConfigurationMessage(String topName) {
        return format(messagesConfig.getString("messages.invalid_top_configuration")).replace("%top%", topName);
    }

    public String getPrizesDistributedMessage(String topName) {
        return format(messagesConfig.getString("messages.prizes_distributed")).replace("%top%", topName);
    }

    public String getNoTopsAvailableMessage() {
        return format(messagesConfig.getString("messages.no_tops_available"));
    }

    public String getTopListHeader() {
        return format(messagesConfig.getString("messages.top_list_header"));
    }

    public List<String> getTopInfoMessage(String topName) {
        return messagesConfig.getStringList("messages.top_info_" + topName.toLowerCase());
    }

    public String format(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message)
                .replace("&#", "§x")
                .replaceAll("([a-fA-F0-9])([a-fA-F0-9])", "§$1§$2");
    }
}