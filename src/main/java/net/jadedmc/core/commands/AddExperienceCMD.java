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
import net.jadedmc.core.player.JadedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddExperienceCMD extends AbstractCommand {
    private final JadedMCPlugin plugin;

    public AddExperienceCMD(@NotNull final JadedMCPlugin plugin) {
        super("addexperience", "jadedcore.addexperience", true);
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull final CommandSender sender, final String[] args) {
        if(args.length < 2) {
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            return;
        }

        int experience = Integer.parseInt(args[1]);
        final JadedPlayer levelsPlayer = plugin.getJadedPlayerManager().getPlayer(target);
        levelsPlayer.addExperience(experience);
    }
}