package org.fun.tops.Handler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.fun.tops.FunTops;
import org.fun.tops.LoggerUtil;
import org.fun.tops.Messages;
import org.fun.tops.Handler.PlaceholderHandler;

import java.util.List;

public class TopCommandHandler implements CommandExecutor {
    private final FunTops plugin;
    private final Messages messages;
    private final LoggerUtil logger;

    public TopCommandHandler(FunTops plugin) {
        this.plugin = plugin;
        this.messages = new Messages(plugin);
        this.logger = new LoggerUtil(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("funtops.admin")) {
            sender.sendMessage(messages.getNoPermissionMessage());
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(messages.getHelpMessage());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "top":
                handleTopCommand(sender, args);
                break;
            case "reload":
                reloadConfig(sender);
                break;
            case "help":
                sender.sendMessage(messages.getHelpMessage());
                break;
            default:
                sender.sendMessage(messages.getUnknownCommandMessage());
        }
        return true;
    }

    private void handleTopCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(messages.getInsufficientArgumentsMessage());
            return;
        }

        String action = args[1];
        switch (action.toLowerCase()) {
            case "close":
                if (args.length < 3) {
                    sender.sendMessage(messages.getSpecifyTopMessage());
                    return;
                }
                String topName = args[2];
                distributePrizes(topName, sender);
                break;
            case "list":
                showTopList(sender);
                break;
            case "info":
                if (args.length < 3) {
                    sender.sendMessage(messages.getSpecifyTopMessage());
                    return;
                }
                String infoTopName = args[2];
                showTopInfo(sender, infoTopName);
                break;
            default:
                sender.sendMessage(messages.getUnknownActionMessage());
        }
    }

    private void distributePrizes(String topName, CommandSender sender) {
        // Используем явные типы вместо var
        org.bukkit.configuration.ConfigurationSection config = plugin.getConfig();
        org.bukkit.configuration.ConfigurationSection topSection = config.getConfigurationSection(topName);

        if (topSection == null) {
            sender.sendMessage(messages.getTopNotFoundMessage(topName));
            return;
        }

        org.bukkit.configuration.ConfigurationSection prizes = topSection.getConfigurationSection("prizes");
        org.bukkit.configuration.ConfigurationSection placeholders = topSection.getConfigurationSection("placeholders");

        if (prizes == null || placeholders == null) {
            sender.sendMessage(messages.getInvalidTopConfigurationMessage(topName));
            return;
        }

        for (String place : prizes.getKeys(false)) {
            String placeholder = placeholders.getString(place);
            if (placeholder == null) continue;

            String playerName = PlaceholderHandler.parsePlaceholders(placeholder, "");
            if (playerName == null || playerName.isEmpty()) continue;

            String commandToExecute = prizes.getString(place);
            if (commandToExecute == null) continue;

            commandToExecute = commandToExecute.replace("%player%", playerName);
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), commandToExecute);

            logger.log("Приз за место " + place + " в топе " + topName + " выдан игроку " + playerName);
        }

        sender.sendMessage(messages.getPrizesDistributedMessage(topName));
    }

    private void showTopList(CommandSender sender) {
        org.bukkit.configuration.ConfigurationSection config = plugin.getConfig();
        List<String> topNames = (List<String>) config.getKeys(false);

        if (topNames.isEmpty()) {
            sender.sendMessage(messages.getNoTopsAvailableMessage());
            return;
        }

        sender.sendMessage(messages.getTopListHeader());
        for (String topName : topNames) {
            sender.sendMessage(messages.format("&e- " + topName));
        }
    }

    private void showTopInfo(CommandSender sender, String topName) {
        org.bukkit.configuration.ConfigurationSection config = plugin.getConfig();
        org.bukkit.configuration.ConfigurationSection topSection = config.getConfigurationSection(topName);

        if (topSection == null) {
            sender.sendMessage(messages.getTopNotFoundMessage(topName));
            return;
        }

        org.bukkit.configuration.ConfigurationSection placeholders = topSection.getConfigurationSection("placeholders");

        if (placeholders == null) {
            sender.sendMessage(messages.getInvalidTopConfigurationMessage(topName));
            return;
        }

        List<String> topMessage = messages.getTopInfoMessage(topName);
        for (String line : topMessage) {
            for (String place : placeholders.getKeys(false)) {
                String placeholder = placeholders.getString(place);
                String playerName = PlaceholderHandler.parsePlaceholders(placeholder, "");

                line = line.replace("%" + place + "%", playerName != null ? playerName : "---");
            }
            sender.sendMessage(messages.format(line));
        }
    }

    private void reloadConfig(CommandSender sender) {
        plugin.reloadConfig();
        messages.reloadMessages();
        sender.sendMessage(messages.getReloadMessage());
    }
}