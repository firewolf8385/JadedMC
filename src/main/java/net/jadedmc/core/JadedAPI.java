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

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.jadedmc.core.minigames.Minigame;
import net.jadedmc.core.networking.CurrentInstance;
import net.jadedmc.core.networking.Instance;
import net.jadedmc.core.networking.InstanceType;
import net.jadedmc.core.networking.NetworkPlayer;
import net.jadedmc.utils.chat.StringUtils;
import net.jadedmc.utils.player.PlayerMap;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.UUID;

/**
 * A collection of methods for interacting with the JadedMC core.
 */
public class JadedAPI {
    private static JadedMCPlugin plugin = null;

    /**
     * Initializes the API.
     * @param pl Instance of the plugin.
     */
    public static void init(@NotNull final JadedMCPlugin pl) {
        plugin = pl;
    }

    public static CurrentInstance getCurrentInstance() {
        return plugin.getInstanceMonitor().getCurrentInstance();
    }

    public static PlayerMap<NetworkPlayer> getNetworkPlayers() {
        final PlayerMap<NetworkPlayer> players = new PlayerMap<>();

        try(Jedis jedis = plugin.getRedis().jedisPool().getResource()) {
            final Set<String> names = jedis.keys("jadedplayers:*");

            for(final String key : names) {
                final String json = jedis.get(key);
                final Document document = Document.parse(json);

                players.add(new NetworkPlayer(document));
            }
        }

        return players;
    }

    /**
     * Get the Instance object of a given server.
     * @param serverName Name of the server.
     * @return Instance object of the server.
     */
    public static Instance getServer(@NotNull final String serverName) {
        return plugin.getInstanceMonitor().getInstance(serverName);
    }

    public static void sendBungeecordMessage(Player player, String channel, String subChannel, String message) {

        // Creates the message
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        out.writeUTF(message);

        // Sends the message using the first player online.
        player.sendPluginMessage(plugin, channel, out.toByteArray());
    }

    public static void sendProxyMessage(@NotNull final String uuid, @NotNull final String message) {
        plugin.getRedis().publishAsync("proxy", "message " + uuid + " " + message);
    }

    public static void sendToServer(@NotNull final UUID uuid, @NotNull final String server) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getRedis().publish("jadedmc", "connect " + uuid.toString() + " " + server));
    }

    public static void sendToLobby(@NotNull final Player player, final Minigame minigame) {
        plugin.getInstanceMonitor().getInstancesAsync().thenAccept(instances -> {
            String serverName = "";
            {
                int count = 999;

                // Loop through all online servers looking for a server to send the player to
                for (Instance instance : instances) {
                    // Make sure the server is the right mode
                    if (instance.getMinigame() != minigame) {
                        continue;
                    }

                    // Make sure the server isn't a game server.
                    if (instance.getType() != InstanceType.LOBBY && instance.getType() != InstanceType.OTHER) {
                        continue;
                    }

                    // Make sure there is room for another game.
                    if (instance.getOnline() >= instance.getCapacity()) {
                        System.out.println("Not enough room!");
                        continue;
                    }

                    //
                    if (instance.getOnline() < count) {
                        count = instance.getOnline();
                        serverName = instance.getName();
                    }
                }

                // If no server is found, give up ¯\_(ツ)_/¯
                if (count == 999) {
                    System.out.println("No Server Found!");
                    return;
                }

                String finalServerName = serverName;
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    JadedAPI.sendBungeecordMessage(player, "BungeeCord", "Connect", finalServerName);
                });
            }
        }).whenComplete((results, error) -> error.printStackTrace());
    }

    public static int getRequiredExp(int level) {
        return 10000 + (level * 2500);
    }

    public static String getLevelBar(final int currentExperience, final int currentLevel) {
        final int squareCount = 40;
        final int maxExperience = getRequiredExp(currentLevel);
        final int perSquare = maxExperience/squareCount;

        String bar = "&3";

        int squares = 0;
        for(int i = currentExperience; i > 0; i-= perSquare) {
            //bar += "■";
            bar += "|";
            squares++;
        }

        bar += "&7";

        for(int i = squares; i < squareCount; i++) {
            //bar += "■";
            bar += "|";
        }

        return StringUtils.translateLegacyMessage(bar);
    }
}