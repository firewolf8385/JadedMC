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

package net.jadedmc.core.commands;

import net.jadedmc.core.JadedMCPlugin;
import net.jadedmc.utils.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class SaveWorldCMD extends AbstractCommand {
    private final JadedMCPlugin plugin;

    public SaveWorldCMD(@NotNull final JadedMCPlugin plugin) {
        super("saveworld", "jadedcore.saveworld", false);
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull final CommandSender sender, final String[] args) {
        final Player player = (Player) sender;

        // Make sure the world isn't loaded as a lobby.
        if(plugin.getLobbyManager().isLobbyWorld(player.getWorld())) {
            ChatUtils.chat(player, "&c&lError &8» You cannot save this world!");
            return;
        }

        final World world = player.getWorld();

        // Kick all the players out before saving.
        for(final Player inhabitant : world.getPlayers()) {
            plugin.getLobbyManager().sendToLocalLobby(inhabitant);
        }

        // Get important data before killing the world.
        final File worldFolder = world.getWorldFolder();
        final String name = world.getName();

        // Kill the world.
        Bukkit.unloadWorld(world, true);

        // Move the corpse to mongodb.
        plugin.getWorldManager().saveWorld(worldFolder, name);
    }
}