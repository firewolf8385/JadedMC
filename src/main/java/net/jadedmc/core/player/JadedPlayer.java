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

package net.jadedmc.core.player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.jadedmc.core.JadedAPI;
import net.jadedmc.core.JadedMCPlugin;
import net.jadedmc.core.achievements.Achievement;
import net.jadedmc.core.events.LevelUpEvent;
import net.jadedmc.utils.player.PluginPlayer;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Represents a Player on the server. Stores plugin-specific data about them.
 * Should only be created async.
 */
public class JadedPlayer extends PluginPlayer {
    private final JadedMCPlugin plugin;
    private final Player player;
    private Rank rank;

    private boolean spying = false;
    private boolean vanished = false;
    private int experience = 0;
    private int level = 1;
    private Timestamp firstJoined;
    private final Collection<Achievement> achievements = new ArrayList<>();

    /**
     * Creates the JadedPlayer
     * @param plugin Instance of the plugin.
     * @param player Player object to use.
     */
    public JadedPlayer(final JadedMCPlugin plugin, final Player player) {
        super(player.getUniqueId(), player.getName());
        this.plugin = plugin;
        this.player = player;

        // Update the player's rank.
        this.rank = Rank.fromName(LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId()).getPrimaryGroup());

        // Player Info
        try {
            PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM player_info where uuid = ? LIMIT 1");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                level = resultSet.getInt("level");
                experience = resultSet.getInt("experience");
                firstJoined = resultSet.getTimestamp("firstOnline");
            }
            else {
                PreparedStatement statement2 = plugin.getMySQL().getConnection().prepareStatement("INSERT INTO player_info (uuid,username,ip) VALUES (?,?,?)");
                statement2.setString(1, getUniqueId().toString());
                statement2.setString(2, getName());
                statement2.setString(3, player.getAddress().getAddress().getHostAddress());
                statement2.executeUpdate();

                level = 1;
                experience = 0;
                firstJoined = new Timestamp(System.currentTimeMillis());
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }

        // Staff settings.
        if(rank.isStaffRank()) {
            try {
                PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM staff_settings WHERE uuid = ? LIMIT 1");
                statement.setString(1, player.getUniqueId().toString());
                ResultSet results = statement.executeQuery();

                if(results.next()) {
                    spying = results.getBoolean(3);
                    vanished = results.getBoolean(2);
                }
                else {
                    PreparedStatement statement2 = plugin.getMySQL().getConnection().prepareStatement("INSERT INTO staff_settings (uuid) VALUES (?)");
                    statement2.setString(1, player.getUniqueId().toString());
                    statement2.executeUpdate();

                    spying = false;
                    vanished = false;
                }
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        // Achievements
        try {
            PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM player_achievements WHERE uuid = ?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Achievement achievement = plugin.getAchievementManager().getAchievement(resultSet.getString("achievementID"));

                if(achievement != null) {
                    achievements.add(achievement);
                }
            }

            int achievementPoints = getAchievementPoints();
            if(achievementPoints > 0) {
                PreparedStatement updateStatement = plugin.getMySQL().getConnection().prepareStatement("UPDATE player_info SET achievementPoints = ? WHERE uuid = ?");
                updateStatement.setInt(1, achievementPoints);
                updateStatement.setString(2, player.getUniqueId().toString());
                updateStatement.executeUpdate();
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void addExperience(final int experience) {
        setExperience(this.experience + experience);

        while(this.experience >= JadedAPI.getRequiredExp(this.level)) {
            setExperience(this.experience - JadedAPI.getRequiredExp(this.level));
            setLevel(this.level + 1);

            plugin.getServer().getPluginManager().callEvent(new LevelUpEvent(this, this.level));
        }
    }

    public Collection<Achievement> getAchievements() {
        return achievements;
    }

    public int getAchievementPoints() {
        int achievementPoints = 0;
        for(Achievement achievement : achievements) {
            achievementPoints += achievement.getPoints();
        }

        return achievementPoints;
    }

    public int getExperience() {
        return experience;
    }

    public int getLevel() {
        return level;
    }

    public Timestamp getFirstJoined() {
        return firstJoined;
    }

    /**
     * Get the player's displayed username.
     * Works with Nicks if enabled.
     * @return The username that should be displayed.
     */
    public String getName() {
        if(plugin.getHookManager().useHyNick()) {
            return PlaceholderAPI.setPlaceholders(player, "%hynick_name%");
        }

        return player.getName();
    }

    /**
     * Get the player data is being stored for.
     * @return Player of the JadedPlayer.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the rank of the player.
     * @return Player's rank.
     */
    public Rank getRank() {
        if(plugin.getHookManager().useHyNick()) {
            String rankString = PlaceholderAPI.setPlaceholders(player, "%hynick_rank%").toUpperCase();

            if(!Rank.exists(rankString)) {
                return rank;
            }

            return Rank.valueOf(rankString);
        }

        return rank;
    }

    /**
     * Get the player's real name.
     * @return Player's real username.
     */
    public String getRealName() {
        return player.getName();
    }

    /**
     * Get the player's UUID.
     * @return UUID of the player.
     */
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    /**
     * Check if the player has the permissions of a specific rank.
     * @param toCheck Rank to check.
     * @return Whether they have the permissions of it or not.
     */
    public boolean hasRank(Rank toCheck) {
        return (rank.getWeight() >= toCheck.getWeight());
    }

    /**
     * Get whether the player is a staff member.
     * @return If the player is a staff member.
     */
    public boolean isStaffMember() {
        return rank.isStaffRank();
    }

    /**
     * Get if the player is spying on commands.
     *
     * @return Whether they are spying on the commands.
     */
    public boolean isSpying() {
        return spying;
    }

    /**
     * Get if the player is vanished.
     *
     * @return Whether they are vanished.
     */
    public boolean isVanished() {
        return vanished;
    }

    /**
     * Set if the player is spying on commands.
     *
     * @param spying Whether they are spying on commands.
     */
    public void setSpying(boolean spying) {
        this.spying = spying;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement("UPDATE staff_settings SET commandSpy = ? WHERE uuid = ?");
                statement.setBoolean(1, spying);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Set if the player is currently vanished.
     * @param vanished Whether they are vanished.
     */
    public void setVanished(boolean vanished) {
        this.vanished = vanished;

        if (!vanished) {
            for (Player viewer : plugin.getServer().getOnlinePlayers()) {
                if (viewer.equals(player)) {
                    continue;
                }

                viewer.showPlayer(plugin, player);
            }
        }
        else {
            for (Player viewer : plugin.getServer().getOnlinePlayers()) {
                if (viewer.equals(player)) {
                    continue;
                }

                viewer.hidePlayer(plugin, player);
            }
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement("UPDATE staff_settings SET vanish = ? WHERE uuid = ?");
                statement.setBoolean(1, vanished);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Update the player's current rank.
     */
    public void updateRank() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // Update the player's rank.
            this.rank = Rank.fromName(LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId()).getPrimaryGroup());
        });
    }

    public void setExperience(final int experience) {
        this.experience = experience;

        try {
            final PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement("UPDATE jadedlevels_players SET experience = ? WHERE uuid = ?");
            statement.setInt(1, experience);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void setLevel(final int level) {
        this.level = level;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement("UPDATE jadedlevels_players SET level = ? WHERE uuid = ?");
                statement.setInt(1, level);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}