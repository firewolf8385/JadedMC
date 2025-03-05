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

package net.jadedmc.core.lobby;

import net.jadedmc.core.JadedMCPlugin;
import net.jadedmc.core.events.LobbyJoinEvent;
import net.jadedmc.utils.LocationUtils;
import net.jadedmc.utils.items.ItemBuilder;
import net.jadedmc.utils.items.SkullBuilder;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

public class LobbyManager {
    private final JadedMCPlugin plugin;

    public LobbyManager(@NotNull final JadedMCPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets all configured lobby worlds.
     * @return Configured lobby worlds.
     */
    public Collection<World> getLobbyWorlds() {
        final Collection<World> worlds = new HashSet<>();

        for(final String worldName : plugin.getConfigManager().getConfig().getStringList("Lobby.Worlds")) {
            worlds.add(Bukkit.getWorld(worldName));
        }

        return worlds;
    }

    /**
     * Gets the Lobby's spawn.
     * @return Lobby spawn.
     */
    public Location getSpawn() {
        final ConfigurationSection spawnSection = plugin.getConfigManager().getConfig().getConfigurationSection("Lobby.Spawn");

        // Make sure the configuration section was set.
        if(spawnSection == null) {
            return null; // No spawn set.
        }

        // Reads the spawn using LocationUtils#fromConfig.
        return LocationUtils.fromConfig(spawnSection);
    }

    /**
     * Check if the lobby features should even be enabled.
     * @return Whether lobby features are enabled.
     */
    public boolean isEnabled() {
        return plugin.getConfigManager().getConfig().getBoolean("Lobby.Enabled");
    }

    /**
     * Check if a world is a configured lobby world.
     * @param world World to check.
     * @return Whether it is a lobby world or not.
     */
    public boolean isLobbyWorld(final World world) {
        if(!isEnabled()) {
            return false;
        }

        return getLobbyWorlds().contains(world);
    }

    /**
     * Checks if the lobby spawn has been set.
     * @return Whether the lobby spawn was set.
     */
    public boolean isSpawnSet() {
        return plugin.getConfigManager().getConfig().getBoolean("Lobby.Spawn.Set");
    }

    /**
     * Sends a given player to the lobby.
     * @param player Player being sent to the lobby.
     */
    public void sendToLocalLobby(@NotNull final Player player) {
        // Reset Player
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

        player.setExp(0);
        player.setLevel(0);
        player.setGameMode(GameMode.ADVENTURE);
        player.setCollidable(true);

        player.setArrowsInBody(0);

        // Teleport Player
        if(isSpawnSet()) {
            player.teleport(getSpawn());
        }

        // Lobby flight
        if(player.hasPermission("jadedlobby.fly")) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        else {
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        // Scoreboard
        if(plugin.getConfigManager().getConfig().getBoolean("Lobby.Scoreboard.Enabled")) {
            new LobbyScoreboard(plugin, player);
        }

        // Give items.
        ItemStack gamesItem = new ItemBuilder(Material.COMPASS)
                .setDisplayName("<green><bold>Games")
                .build();

        ItemStack profileItem = new SkullBuilder()
                .fromPlayer(player)
                .asItemBuilder()
                .setDisplayName("<green><bold>Profile")
                .build();

        ItemStack cosmeticsItem = new ItemBuilder(Material.CHEST)
                .setDisplayName("<green><bold>Cosmetics")
                .build();

        ItemStack settings = new ItemBuilder(Material.COMPARATOR)
                .setDisplayName("<green><bold>Settings")
                .build();

        player.getInventory().clear();

        if(useGamesItem()) player.getInventory().setItem(0, gamesItem);
        if(useProfileItem()) player.getInventory().setItem(1, profileItem);
        if(useCosmeticsItem()) player.getInventory().setItem(2, cosmeticsItem);
        if(useSettingsItem()) player.getInventory().setItem(8, settings);

        // TODO: Properly handle Cosmetics
        // Equips saved cosmetics.
        //UltraPlayer ultraPlayer = UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(player);
        //ultraPlayer.getProfile().equip();

        // LobbyJoinEvent
        plugin.getServer().getPluginManager().callEvent(new LobbyJoinEvent(player));
    }

    /**
     * Sets the location of spawn to a given location.
     * @param location New location of spawn.
     */
    public void setSpawn(final Location location) {
        plugin.getConfigManager().getConfig().set("Lobby.Spawn.World", location.getWorld().getName());
        plugin.getConfigManager().getConfig().set("Lobby.Spawn.X", location.getX());
        plugin.getConfigManager().getConfig().set("Lobby.Spawn.Y", location.getY());
        plugin.getConfigManager().getConfig().set("Lobby.Spawn.Z", location.getZ());
        plugin.getConfigManager().getConfig().set("Lobby.Spawn.Yaw", location.getYaw());
        plugin.getConfigManager().getConfig().set("Lobby.Spawn.Pitch", location.getPitch());
        plugin.getConfigManager().getConfig().set("Lobby.Spawn.Set", true);

        plugin.getConfigManager().reloadConfig();
    }

    /**
     * Check if the cosmetics item should be enabled.
     * @return Wether the cosmetics item is enabled.
     */
    public boolean useCosmeticsItem() {
        return plugin.getConfigManager().getConfig().getBoolean("Lobby.Items.Cosmetics");
    }

    /**
     * Check if the games item should be enabled.
     * @return Whether the games item is enabled.
     */
    public boolean useGamesItem() {
        return plugin.getConfigManager().getConfig().getBoolean("Lobby.Items.Games");
    }

    /**
     * Check if the profile item should be enabled.
     * @return Whether the profile item is enabled.
     */
    public boolean useProfileItem() {
        return plugin.getConfigManager().getConfig().getBoolean("Lobby.Items.Profile");
    }

    /**
     * Check if the settings item should be enabled.
     * @return Whether the settings item is enabled.
     */
    public boolean useSettingsItem() {
        return plugin.getConfigManager().getConfig().getBoolean("Lobby.Items.Settings");
    }
}