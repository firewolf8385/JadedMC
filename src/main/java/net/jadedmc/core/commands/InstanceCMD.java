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
import net.jadedmc.core.networking.InstanceStatus;
import net.jadedmc.utils.chat.ChatUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class InstanceCMD extends AbstractCommand {

    public InstanceCMD() {
        super("instance", "jadedcore.instance", true);
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
        }
    }
}