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

import net.jadedmc.utils.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class runs the feed command, which feeds the player who ran it.
 */
public class FeedCMD extends AbstractCommand {

    /**
     * Creates the /feed command with the permission "jadedcore.feed".
     */
    public FeedCMD() {
        super("feed", "jadedcore.feed", false);
    }

    /**
     * This is the code that runs when the command is sent.
     * @param sender The player (or console) that sent the command.
     * @param args The arguments of the command.
     */
    public void execute(@NotNull final CommandSender sender, final String[] args) {
        final Player player = (Player) sender;

        // Checks if the player meant to feed themselves.
        if(args.length == 0) {
            player.setFoodLevel(20);
            player.setSaturation(20);
            ChatUtils.chat(player, "&a&lFeed &8» &aYou have been fed.");
            return;
        }

        // If not, makes sure they have permission to feed someone else.
        if(!player.hasPermission("jadedcore.feed.other")) {
            ChatUtils.chat(player,"&c&lError &8» &cYou do not have access to that command!");
            return;
        }

        // Gets the player the sender meant to heal.
        final Player target = Bukkit.getPlayer(args[0]);

        // Makes sure they are online.
        if(target == null) {
            ChatUtils.chat(player,"&c&lError &8» &cThat player is not online!");
            return;
        }

        // Heals the player.
        target.setFoodLevel(20);
        target.setSaturation(20);
        ChatUtils.chat(target, "&a&lFeed &8» &aYou have been fed.");
        ChatUtils.chat(player, "&a&lHeal &8» &aYou have fed &f" + target.getName() + "&a.");
    }
}