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

package net.jadedmc.utils.scoreboard;

import fr.mrmicky.fastboard.adventure.FastBoard;
import net.jadedmc.utils.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public abstract class CustomScoreboard {
    private static final HashMap<UUID, CustomScoreboard> players = new HashMap<>();
    private final FastBoard fastBoard;
    private final Player player;

    public CustomScoreboard(@NotNull final Player player) {
        this.player = player;
        this.fastBoard = new FastBoard(player);

        // Destroy old scoreboard when adding a new one.
        if(players.containsKey(player.getUniqueId())) {
            players.get(player.getUniqueId()).destroy();
        }

        players.put(player.getUniqueId(), this);
    }

    public static HashMap<UUID, CustomScoreboard> getPlayers() {
        return new HashMap<>(players);
    }

    public static void removePlayer(@NotNull final UUID uuid) {
        players.remove(uuid);
    }

    public void destroy() {
        this.fastBoard.delete();
    }

    public FastBoard getFastBoard() {
        return this.fastBoard;
    }

    public void removeLine(final int line) {
        this.fastBoard.removeLine(line);
    }

    public void setLine(final int line, @NotNull final String text) {
        final Component component = ChatUtils.translate(ChatUtils.parsePlaceholders(player, text));
        this.fastBoard.updateLine(line, component);
    }

    public void setTitle(@NotNull final String title) {
        final Component component = ChatUtils.translate(ChatUtils.parsePlaceholders(player, title));
        this.fastBoard.updateTitle(component);
    }

    public abstract void update(@NotNull final Player player);
}