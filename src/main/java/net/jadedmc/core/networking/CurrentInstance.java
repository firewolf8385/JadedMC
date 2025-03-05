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

package net.jadedmc.core.networking;

import net.jadedmc.core.JadedMCPlugin;
import net.jadedmc.core.minigames.Minigame;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Stores information from the current server instance, as obtained through Redis.
 */
public class CurrentInstance {
    private final JadedMCPlugin plugin;
    private final String name;
    private final long startTime;
    private InstanceStatus status;
    private final Minigame minigame;
    private final InstanceType instanceType;
    private final int majorVersion;
    private final int minorVersion;
    private String address;

    /**
     * Creates the CurrentInstance object.
     * @param plugin Instance of the plugin.
     */
    public CurrentInstance(@NotNull final JadedMCPlugin plugin) {
        this.plugin = plugin;
        this.name = plugin.getConfigManager().getConfig().getString("serverName");
        this.startTime = System.currentTimeMillis();
        this.status = InstanceStatus.ONLINE;
        this.minigame = Minigame.valueOf(plugin.getConfigManager().getConfig().getString("serverGame"));

        String version = Bukkit.getServer().getBukkitVersion().split("-")[0];
        this.majorVersion = Integer.parseInt(version.split("\\.")[1]);
        this.minorVersion = Integer.parseInt(version.split("\\.")[2]);

        if(plugin.getConfigManager().getConfig().isSet("serverType")) {
            this.instanceType = InstanceType.valueOf(plugin.getConfigManager().getConfig().getString("serverType"));
        }
        else if(plugin.getLobbyManager().isEnabled()) {
            this.instanceType = InstanceType.LOBBY;
        }
        else {
            this.instanceType = InstanceType.GAME;
        }

        if(plugin.getConfigManager().getConfig().isSet("status")) {
            this.status = InstanceStatus.valueOf(plugin.getConfigManager().getConfig().getString("status"));
        }

        // Get the IP address of the machine it's being run on.
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            this.address = socket.getLocalAddress().getHostAddress();
        }
        catch (UnknownHostException | SocketException e) {
            this.address = "0.0.0.0";
        }
    }

    /**
     * Gets the address of the machine the instance is running on.
     * @return Instance address.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Get the maximum capacity of the Instance.
     * @return Maximum number of players the Instance can hold.
     */
    public int getCapacity() {
        return plugin.getServer().getMaxPlayers();
    }

    /**
     * Get the major version of the server.
     * E.G 1.X.4
     * @return Server major version.
     */
    public int getMajorVersion() {
        return majorVersion;
    }

    /**
     * Get the Minigame being run on the Instance.
     * @return Instance Minigame.
     */
    public Minigame getMinigame() {
        return minigame;
    }

    /**
     * Get the minor version of the server.
     * E.G 1.20.X
     * @return Server minor version.
     */
    public int getMinorVersion() {
        return minorVersion;
    }

    /**
     * Get the name of the Instance.
     * @return Instance name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the number of players currently on the Instance.
     * @return Players online.
     */
    public int getOnline() {
        return plugin.getServer().getOnlinePlayers().size();
    }

    /**
     * Get the number of slots available for players to join.
     * @return Number of open spots.
     */
    public int getOpenSlots() {
        return getCapacity() - getOnline();
    }

    /**
     * Get the port the Instance is running on.
     * @return Port of the Instance.
     */
    public int getPort() {
        return plugin.getServer().getPort();
    }

    /**
     * Get the time (in milliseconds since epoch) that the server was started.
     * @return Server start time.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Gets the current status of the Instance.
     * @return Instance Status.
     */
    public InstanceStatus getStatus() {
        return status;
    }

    /**
     * Get the type of server that is being run.
     * Can be GAME, LOBBY, or OTHER.
     * @return Server type.
     */
    public InstanceType getType() {
        return instanceType;
    }

    /**
     * Get how long (in ms) the server has been up for.
     * @return Server uptime in milliseconds.
     */
    public long getUptime() {
        return System.currentTimeMillis() - this.startTime;
    }

    /**
     * Sends a heartbeat message to Redis, containing various data about the instance.
     */
    public void heartbeat() {
        final Document document = new Document()
                .append("serverName", name)
                .append("status", getStatus().toString())
                .append("online", getOnline())
                .append("capacity", getCapacity())
                .append("mode", getMinigame().toString())
                .append("heartbeat", System.currentTimeMillis())
                .append("address", getAddress())
                .append("port", getPort())
                .append("type", getType().toString())
                .append("startTime", getStartTime())
                .append("majorVersion", majorVersion)
                .append("minorVersion", minorVersion);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getRedis().set("servers:" + plugin.getConfigManager().getConfig().getString("serverName"), document.toJson());
        });
    }

    /**
     * Updates the status of the instance.
     * @param status New Instance Status.
     */
    public void setStatus(InstanceStatus status) {
        // If the server is closed and empty, shut it down.
        if(status == InstanceStatus.CLOSED && getOnline() == 0) {
            plugin.getServer().shutdown();
            return;
        }

        this.status = status;
    }
}