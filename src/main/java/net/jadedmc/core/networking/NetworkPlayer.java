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

import net.jadedmc.core.JadedAPI;
import net.jadedmc.utils.player.PluginPlayer;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a player anywhere on the JadedMC network.
 * Data is stored in Redis
 */
public class NetworkPlayer extends PluginPlayer {
    private final String skin;
    private final String serverName;

    /**
     * Creates a new NetworkPlayer using a Bson document.
     * @param document Document to load the player from.
     */
    public NetworkPlayer(@NotNull final Document document) {
        super(UUID.fromString(document.getString("uuid")), document.getString("displayName"));
        this.skin = document.getString("skin");
        this.serverName = document.getString("server");
    }

    /**
     * Gets the skin of the player.
     * @return Player's skin.
     */
    public String getSkin() {
        return this.skin;
    }

    /**
     * Get the instance of the server the player is currently on.
     * @return Player's server instance.
     */
    public Instance getServer() {
        return JadedAPI.getServer(serverName);
    }

    /**
     * Gets the name of the server the player is currently on.
     * @return Player's server.
     */
    public String getServerName() {
        return this.serverName;
    }
}