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
import net.jadedmc.core.networking.Instance;
import net.jadedmc.utils.gui.CustomGUI;
import net.jadedmc.utils.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InstancesCMD extends AbstractCommand {
    private final JadedMCPlugin plugin;

    public InstancesCMD(final JadedMCPlugin plugin) {
        super("instances", "jadedcore.instances", false);
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull final CommandSender sender, final String[] args) {
        final Player player = (Player) sender;
        new InstancesGUI(plugin).open(player);
    }

    private static class InstancesGUI extends CustomGUI {

        public InstancesGUI(@NotNull final JadedMCPlugin plugin) {
            super(54, "Instances");

            plugin.getInstanceMonitor().getInstancesAsync().thenAccept(instances -> {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    int slot = 0;

                    for(final Instance instance : instances) {
                        String color;
                        Material xMaterial;

                        switch (instance.getStatus()) {
                            case UNRESPONSIVE -> {
                                color = "<dark_gray>";
                                xMaterial = Material.BLACK_TERRACOTTA;
                            }

                            case FULL -> {
                                color = "<red>";
                                xMaterial = Material.RED_TERRACOTTA;
                            }

                            case CLOSED -> {
                                color = "<gold>";
                                xMaterial = Material.ORANGE_TERRACOTTA;
                            }

                            case MAINTENANCE -> {
                                color = "<light_purple>";
                                xMaterial = Material.PURPLE_TERRACOTTA;
                            }

                            default -> {
                                color = "<green>";
                                xMaterial = Material.GREEN_TERRACOTTA;
                            }
                        }

                        ItemBuilder builder = new ItemBuilder(xMaterial)
                                .setDisplayName(color + instance.getName())
                                .addLore("<gray>Mode: " + color + instance.getMinigame().toString())
                                .addLore("<gray>Type: " + color + instance.getType().toString())
                                .addLore("<gray>Online: " + color + instance.getOnline() + "<gray>/" + color + instance.getCapacity())
                                .addLore("<gray>Address: " + color + instance.getAddress() + ":"  + instance.getPort());
                        setItem(slot, builder.build());

                        slot++;
                    }
                });
            });
        }
    }
}