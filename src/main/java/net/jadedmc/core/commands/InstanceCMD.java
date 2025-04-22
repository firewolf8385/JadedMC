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

import net.jadedmc.core.JadedAPI;
import net.jadedmc.core.JadedMCPlugin;
import net.jadedmc.core.networking.InstanceStatus;
import net.jadedmc.core.networking.InstanceType;
import net.jadedmc.jadedchat.JadedChat;
import net.jadedmc.utils.chat.ChatUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class InstanceCMD extends AbstractCommand {
    private final JadedMCPlugin plugin;

    public InstanceCMD(@NotNull final JadedMCPlugin plugin) {
        super("instance", "jadedcore.instance", true);
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull final CommandSender sender, final String[] args) {
        if(args.length == 0) {
            return;
        }

        switch (args[0].toLowerCase()) {
            case "close" -> {
                JadedAPI.getCurrentInstance().setStatus(InstanceStatus.CLOSED);
                ChatUtils.chat(sender, "<green><bold>Instance</bold> <dark_gray>» <green>Instance has been marked as closed.");
            }

            case "memory" -> {
                ChatUtils.chat(sender, "<green><bold>Instance</bold> <dark_gray>» <green>Max: " + Runtime.getRuntime().maxMemory());
                ChatUtils.chat(sender, "<green><bold>Instance</bold> <dark_gray>» <green>Free: " + Runtime.getRuntime().freeMemory());
            }

            case "check" -> {
                boolean passed = true;

                if(Runtime.getRuntime().maxMemory() == 1073741824) {
                    ChatUtils.chat(sender, "<red><bold>Instance</bold> <dark_gray>» <red>Maximum memory set to 1 GB!");
                    passed = false;
                }

                if(plugin.getServer().getMaxPlayers() == 20) {
                    ChatUtils.chat(sender, "<red><bold>Instance</bold> <dark_gray>» <red>Maximum players set to 20!");
                    passed = false;
                }

                if(plugin.getServer().getAllowNether()) {
                    ChatUtils.chat(sender, "<red><bold>Instance</bold> <dark_gray>» <red>The nether is enabled!");
                    passed = false;
                }

                if(plugin.getServer().getAllowEnd()) {
                    ChatUtils.chat(sender, "<red><bold>Instance</bold> <dark_gray>» <red>The end is enabled!");
                    passed = false;
                }

                if(!plugin.getServer().spigot().getSpigotConfig().getBoolean("advancements.disable-saving")) {
                    ChatUtils.chat(sender, "<red><bold>Instance</bold> <dark_gray>» <red>Advancements saving is enabled!");
                    passed = false;
                }

                if(!plugin.getServer().spigot().getSpigotConfig().getBoolean("players.disable-saving")) {
                    ChatUtils.chat(sender, "<red><bold>Instance</bold> <dark_gray>» <red>Player saving is enabled!");
                    passed = false;
                }

                if(!plugin.getServer().spigot().getSpigotConfig().getBoolean("stats.disable-saving")) {
                    ChatUtils.chat(sender, "<red><bold>Instance</bold> <dark_gray>» <red>Stats saving is enabled!");
                    passed = false;
                }

                if(!plugin.getServer().getPluginManager().isPluginEnabled("JadedSync")) {
                    ChatUtils.chat(sender, "<red><bold>Instance</bold> <dark_gray>» <red>JadedSync is not installed!");
                    passed = false;
                }

                if(!plugin.getServer().getPluginManager().isPluginEnabled("JadedParty")) {
                    ChatUtils.chat(sender, "<red><bold>Instance</bold> <dark_gray>» <red>JadedParty is not installed!");
                    passed = false;
                }

                if(JadedChat.getServer().equalsIgnoreCase("server")) {
                    ChatUtils.chat(sender, "<red><bold>Instance</bold> <dark_gray>» <red>JadedChat server name not set!");
                    passed = false;
                }

                if(!JadedChat.isMySQLEnabled()) {
                    ChatUtils.chat(sender, "<red><bold>Instance</bold> <dark_gray>» <red>JadedChat MySQL is not set!");
                    passed = false;
                }

                if(plugin.getInstanceMonitor().getCurrentInstance().getType() == InstanceType.LOBBY && !plugin.getServer().getPluginManager().isPluginEnabled("DiscordSRV")) {
                    ChatUtils.chat(sender, "<red><bold>Instance</bold> <dark_gray>» <red>DiscordSRV is not installed!");
                    passed = false;
                }

                if(passed) {
                    ChatUtils.chat(sender, "<green><bold>Instance</bold> <dark_gray>» <green>Instance has passed all setup checks.");
                }
            }
        }
    }
}