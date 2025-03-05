/*
 * This file is part of JadedMC, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.core.settings;

import net.jadedmc.core.JadedMCPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Allows easy access to plugin configuration
 * files. Stores spawn and arena locations.
 */
public class ConfigManager {
    private final File configFile;
    private FileConfiguration config;

    /**
     * Creates the SettingsManager and loads each config file.
     * @param plugin Instance of the plugin.
     */
    public ConfigManager(@NotNull final JadedMCPlugin plugin) {
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        configFile = new File(plugin.getDataFolder(), "config.yml");
        plugin.saveConfig();
    }

    /**
     * Get the main configuration file.
     * @return Main configuration file.
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Update the configuration files.
     */
    public void reloadConfig() {
        saveConfig();
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Saves the current configuration.
     */
    public void saveConfig() {
        try {
            config.save(configFile);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}