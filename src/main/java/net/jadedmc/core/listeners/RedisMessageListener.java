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
import net.jadedmc.core.events.RedisMessageEvent;
import net.jadedmc.core.networking.InstanceStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class RedisMessageListener implements Listener {
    private final JadedMCPlugin plugin;

    public RedisMessageListener(final JadedMCPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMessage(RedisMessageEvent event) {
        if(!event.getChannel().equalsIgnoreCase("jadedmc")) {
            return;
        }

        String[] args = event.getMessage().split(" ");

        if(args.length < 2) {
            return;
        }

        switch (args[1]) {
            case "summon" -> {
                UUID playerUUID = UUID.fromString(args[2]);
                String server = args[3];

                Player player = plugin.getServer().getPlayer(playerUUID);

                if(player == null || !player.isOnline()) {
                    return;
                }

                JadedAPI.sendBungeecordMessage(player, "BungeeCord", "connect", server);
            }

            case "close" -> {
                String server = args[2];

                if(server.equalsIgnoreCase(JadedAPI.getCurrentInstance().getName())) {
                    JadedAPI.getCurrentInstance().setStatus(InstanceStatus.CLOSED);
                }
            }
        }
    }
}