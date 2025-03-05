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

import me.clip.placeholderapi.PlaceholderAPI;
import net.jadedmc.core.JadedMCPlugin;
import net.jadedmc.utils.scoreboard.CustomScoreboard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LobbyScoreboard extends CustomScoreboard {
    private final JadedMCPlugin plugin;

    public LobbyScoreboard(@NotNull final JadedMCPlugin plugin, @NotNull final Player player) {
        super(player);
        this.plugin = plugin;
    }

    @Override
    public void update(@NotNull final Player player) {
        // Sets up the scoreboard.
        setTitle(plugin.getConfigManager().getConfig().getString("Lobby.Scoreboard.Title"));

        List<String> lines = plugin.getConfigManager().getConfig().getStringList("Lobby.Scoreboard.Lines");

        int lineNumber = 0;
        for(String line : lines) {
            setLine(lineNumber, PlaceholderAPI.setPlaceholders(player, line));

            lineNumber++;
        }

        for(int i = lineNumber; i <= 14; i++) {
            removeLine(i);
        }
    }
}