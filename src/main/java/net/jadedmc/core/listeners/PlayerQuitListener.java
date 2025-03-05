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

import net.jadedmc.core.JadedAPI;
import net.jadedmc.core.JadedMCPlugin;
import net.jadedmc.core.events.LobbyQuitEvent;
import net.jadedmc.core.networking.InstanceStatus;
import net.jadedmc.core.player.JadedPlayer;
import net.jadedmc.utils.chat.ChatUtils;
import net.jadedmc.utils.scoreboard.CustomScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens to the PlayerQuitEvent, which is called when a player quits.
 * Used to remove references to the player object when the player leaves.
 */
public class PlayerQuitListener implements Listener {
    private final JadedMCPlugin plugin;

    /**
     * Creates the Listener.
     * @param plugin Instance of the plugin.
     */
    public PlayerQuitListener(JadedMCPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the PlayerQuitEvent is called.
     * @param event PlayerQuitEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        JadedPlayer jadedPlayer = plugin.getJadedPlayerManager().getPlayer(player);

        // Call the lobby quit event if the world is a lobby world.
        if(plugin.getLobbyManager().isLobbyWorld(player.getWorld())) {
            plugin.getServer().getPluginManager().callEvent(new LobbyQuitEvent(player));
        }

        event.quitMessage(null);

        if(!jadedPlayer.isVanished()) {
            ChatUtils.broadcast(player.getWorld(),"&8[&c-&8] &c" + jadedPlayer.getName());
        }

        plugin.getJadedPlayerManager().removePlayer(player);

        // Remove cached scoreboard from a player.
        CustomScoreboard.removePlayer(player.getUniqueId());

        // Shut down the server if it is closed and empty.
        if(plugin.getServer().getOnlinePlayers().size() == 1 && JadedAPI.getCurrentInstance().getStatus() == InstanceStatus.CLOSED) {
            System.out.println("Empty Server Detected. Waiting 15 seconds before shutdown.");
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getServer().shutdown(),15*20);
        }
    }
}