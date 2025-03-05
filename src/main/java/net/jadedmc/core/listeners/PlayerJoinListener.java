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

package net.jadedmc.core.listeners;

import net.jadedmc.core.JadedMCPlugin;
import net.jadedmc.core.events.JadedJoinEvent;
import net.jadedmc.core.player.JadedPlayer;
import net.jadedmc.core.player.Rank;
import net.jadedmc.utils.chat.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

/**
 * Listens to the PlayerJoinEvent, which is called when a player joins the server.
 * Used to load the JadedPlayer of that player.
 */
public class PlayerJoinListener implements Listener {
    private final JadedMCPlugin plugin;

    /**
     * Creates the Listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerJoinListener(JadedMCPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the PlayerJoinEvent is called.
     * @param event PlayerJoinEvent.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();

        // Hide all vanished players.
        for(JadedPlayer otherPlayer : plugin.getJadedPlayerManager().getJadedPlayers()) {
            if(otherPlayer.isVanished()) {
                player.hidePlayer(plugin, otherPlayer.getPlayer());
            }
        }

        // Load the JadedPlayer.
        plugin.getJadedPlayerManager().addPlayer(player).thenAccept(jadedPlayer -> {

            // Join Message
            if(plugin.getLobbyManager().isEnabled() && !jadedPlayer.isVanished()) {
                final Rank rank = jadedPlayer.getRank();
                switch (rank) {
                    //case AMETHYST -> ChatUtils.broadcast(player.getWorld(), "&5>&f>&5> &lAmethyst &7" + jadedPlayer.getName() + " &ahas joined the lobby! &5<&f<&5<");
                    //case SAPPHIRE -> ChatUtils.broadcast(player.getWorld(), "&9>&f>&9> &lSapphire &7" + jadedPlayer.getName() + " &ahas joined the lobby! &9<&f<&9<");
                    //case JADED -> ChatUtils.broadcast(player.getWorld(), "&a>&f>&a> &lJaded &7" + jadedPlayer.getName() + " &ahas joined the lobby! &a<&f<&a<");
                    case AMETHYST, SAPPHIRE, JADED -> ChatUtils.broadcast(player.getWorld(), rank.getRankColor() + "><white>></white>> " + rank.getDisplayName() + " <gray>" + jadedPlayer.getName() + " <green>has joined the lobby! " + rank.getRankColor() + "<<white><</white><");
                    default -> ChatUtils.broadcast(player.getWorld(), "&8[&a+&8] &a" + jadedPlayer.getName());
                }
            }

            // Give the "A Whole New World" achievement.
            plugin.getAchievementManager().getAchievement("general_1").unlock(player);

            // Give the "Veteran" achievement.
            if(System.currentTimeMillis() - jadedPlayer.getFirstJoined().getTime() > Long.parseLong("31556952000")) {
                plugin.getAchievementManager().getAchievement("general_5").unlock(player);
            }

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                // If vanished, hide the player from all other players.
                if(jadedPlayer.isVanished()) {
                    for(Player otherPlayer : plugin.getServer().getOnlinePlayers()) {
                        otherPlayer.hidePlayer(plugin, player);
                    }
                }

                plugin.getServer().getPluginManager().callEvent(new JadedJoinEvent(jadedPlayer));

                // Sends the player to the lobby if it is enabled.
                if(plugin.getLobbyManager().isEnabled()) {
                    plugin.getLobbyManager().sendToLocalLobby(player);
                }
            });
        });
    }
}
