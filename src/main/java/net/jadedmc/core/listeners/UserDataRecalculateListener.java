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

package net.jadedmc.core.listeners;

import net.jadedmc.core.JadedMCPlugin;
import net.jadedmc.core.player.JadedPlayer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;

public class UserDataRecalculateListener {
    public UserDataRecalculateListener(JadedMCPlugin plugin, LuckPerms luckPerms) {
        EventBus eventBus = luckPerms.getEventBus();
        eventBus.subscribe(plugin, UserDataRecalculateEvent.class, event -> {
            JadedPlayer jadedPlayer = plugin.getJadedPlayerManager().getPlayer(event.getUser().getUniqueId());

            if(jadedPlayer == null) {
                return;
            }

            jadedPlayer.updateRank();
        });
    }
}