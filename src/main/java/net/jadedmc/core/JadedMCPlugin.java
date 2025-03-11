/*
 * This file is part of JadedMC, licensed under the MIT License.
 *
 * Copyright (c) JadedMC
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jadedmc.core;

import net.jadedmc.core.achievements.AchievementManager;
import net.jadedmc.core.commands.AbstractCommand;
import net.jadedmc.core.database.MongoDB;
import net.jadedmc.core.database.MySQL;
import net.jadedmc.core.database.Redis;
import net.jadedmc.core.leaderboards.LeaderboardManager;
import net.jadedmc.core.listeners.*;
import net.jadedmc.core.lobby.LobbyManager;
import net.jadedmc.core.networking.InstanceMonitor;
import net.jadedmc.core.player.JadedPlayerManager;
import net.jadedmc.core.settings.ConfigManager;
import net.jadedmc.core.settings.HookManager;
import net.jadedmc.core.worlds.WorldManager;
import net.jadedmc.utils.FileUtils;
import net.jadedmc.utils.gui.GUIListeners;
import net.jadedmc.utils.scoreboard.ScoreboardUpdate;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class JadedMCPlugin extends JavaPlugin {
    private AchievementManager achievementManager;
    private ConfigManager configManager;
    private InstanceMonitor instanceMonitor;
    private LeaderboardManager leaderboardManager;
    private LobbyManager lobbyManager;
    private HookManager hookManager;
    private JadedPlayerManager jadedPlayerManager;
    private MongoDB mongoDB;
    private MySQL mySQL;
    private Redis redis;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        JadedAPI.init(this);

        // Load server settings.
        configManager = new ConfigManager(this);
        hookManager = new HookManager(this);

        // Load Databases
        mongoDB = new MongoDB(this);
        mySQL = new MySQL(this);
        mySQL.openConnection();
        redis = new Redis(this);

        // Load other stuff
        jadedPlayerManager = new JadedPlayerManager(this);
        worldManager = new WorldManager(this);
        lobbyManager = new LobbyManager(this);
        achievementManager = new AchievementManager(this);
        leaderboardManager = new LeaderboardManager(this);

        // Allow the plugin to send messages to the proxy.
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Updates scoreboards every second
        new ScoreboardUpdate().runTaskTimer(this, 20L, 20L);

        instanceMonitor = new InstanceMonitor(this);

        registerListeners();
        AbstractCommand.registerCommands(this);

        // Register placeholders.
        new Placeholders(this).register();

        // Downloads configured worlds.
        updateWorlds();
    }

    @Override
    public void onDisable() {
        // Deletes the search from Redis
        redis.del("servers:" + configManager.getConfig().getString("serverName"));
    }

    /**
     * Registers plugin listeners.
     */
    private void registerListeners() {
        // Plugin listeners.
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new ChannelMessageSendListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChangeWorldListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandPreprocessListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new RedisMessageListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldInitListener(this), this);
        new UserDataRecalculateListener(this, LuckPermsProvider.get());

        // Utility listeners.
        getServer().getPluginManager().registerEvents(new GUIListeners(), this);
    }

    /**
     * Updates worlds listed in the config file.
     * Called when the server is first started.
     */
    private void updateWorlds() {
        ConfigurationSection worldsSection = configManager.getConfig().getConfigurationSection("Worlds");

        // Exit if there is no section.
        if(worldsSection == null) {
            return;
        }

        // Loops through each world configured.
        for(String world : worldsSection.getKeys(false)) {
            String fileName = configManager.getConfig().getString("Worlds." + world);

            // Deletes the world folder if it currently already exists.
            File worldFolder = new File(getServer().getPluginsFolder().getParent(), world);
            if(worldFolder.exists()) {
                FileUtils.deleteDirectory(worldFolder);
            }

            // Downloads the world from MongoDB.
            File newWorld = worldManager.downloadWorldSync(fileName);
            newWorld.renameTo(worldFolder);
        }
    }

    public AchievementManager getAchievementManager() {
        return this.achievementManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public HookManager getHookManager() {
        return this.hookManager;
    }

    public InstanceMonitor getInstanceMonitor() {
        return this.instanceMonitor;
    }

    public JadedPlayerManager getJadedPlayerManager() {
        return this.jadedPlayerManager;
    }

    public LeaderboardManager getLeaderboardManager() {
        return this.leaderboardManager;
    }

    public LobbyManager getLobbyManager() {
        return this.lobbyManager;
    }

    public MongoDB getMongoDB() {
        return this.mongoDB;
    }

    public MySQL getMySQL() {
        return this.mySQL;
    }

    public Redis getRedis() {
        return this.redis;
    }

    public WorldManager getWorldManager() {
        return this.worldManager;
    }
}