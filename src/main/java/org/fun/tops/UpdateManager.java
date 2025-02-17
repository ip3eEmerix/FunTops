package org.fun.tops;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateManager {
    private final JavaPlugin plugin;
    private final String currentVersion;
    private final boolean autoUpdate;
    private final String updateUrl = "https://api.github.com/repos/YourUsername/FunTops/releases/latest";

    public UpdateManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getConfig().getString("plugin-v");
        this.autoUpdate = plugin.getConfig().getBoolean("auto-update");
        checkForUpdates();
    }

    private void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Получаем информацию о последней версии
                URL url = new URL(updateUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

                if (connection.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(reader);

                    String latestVersion = ((String) json.get("tag_name")).replace("v", "");
                    String downloadUrl = ((JSONObject) ((JSONObject) json.get("assets")).get(0)).get("browser_download_url").toString();

                    if (!currentVersion.equals(latestVersion)) {
                        plugin.getLogger().warning("Обнаружена новая версия плагина: v" + latestVersion);
                        plugin.getLogger().warning("Текущая версия: v" + currentVersion);

                        if (autoUpdate) {
                            plugin.getLogger().info("Автоматическое обновление включено. Загрузка новой версии...");
                            downloadUpdate(downloadUrl);
                        } else {
                            plugin.getLogger().info("Чтобы обновить плагин, включите auto-update: true в config.yml.");
                        }
                    } else {
                        plugin.getLogger().info("Плагин уже использует последнюю версию: v" + currentVersion);
                    }

                    reader.close();
                } else {
                    plugin.getLogger().warning("Не удалось проверить обновления. Код ответа: " + connection.getResponseCode());
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Ошибка при проверке обновлений: " + e.getMessage());
            }
        });
    }

    private void downloadUpdate(String downloadUrl) {
        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                File pluginFile = new File(plugin.getDataFolder().getParentFile(), plugin.getName() + ".jar");
                File tempFile = new File(plugin.getDataFolder().getParentFile(), plugin.getName() + "-temp.jar");

                try (InputStream in = connection.getInputStream(); FileOutputStream out = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }

                plugin.getLogger().info("Новая версия успешно загружена. Перезапустите сервер для применения изменений.");
                plugin.getLogger().info("Старый файл плагина будет заменён после перезапуска.");

                // Переименовываем временный файл
                if (pluginFile.exists()) {
                    pluginFile.delete();
                }
                tempFile.renameTo(pluginFile);
            } else {
                plugin.getLogger().warning("Не удалось загрузить обновление. Код ответа: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка при загрузке обновления: " + e.getMessage());
        }
    }
}