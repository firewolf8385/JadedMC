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

package net.jadedmc.core.minigames;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Represents one of the various minigames that can be played throughout the server.
 */
public enum Minigame {
    BUILD("", Material.BEDROCK),
    CACTUS_RUSH("Cactus Rush", Material.CACTUS),
    CREATIVE("", Material.BEDROCK),
    ELYTRAPVP("", Material.BEDROCK),
    HOUSING("", Material.BEDROCK),
    HUB("Main Lobby", Material.CRAFTING_TABLE),
    LIMBO("Limbo", Material.BEDROCK),
    TURF_WARS("", Material.BEDROCK),
    DUELS("", Material.BEDROCK),
    TOURNAMENTS("", Material.BEDROCK),
    DUELS_LEGACY("", Material.BEDROCK),
    DUELS_MODERN("", Material.BEDROCK),
    TOURNAMENTS_LEGACY("", Material.BEDROCK),
    TOURNAMENTS_MODERN("", Material.BEDROCK),
    GENERAL("General", Material.BOOK);

    private final String name;
    private final Material iconMaterial;

    Minigame(@NotNull final String name, final Material iconMaterial) {
        this.name = name;
        this.iconMaterial = iconMaterial;
    }

    public Material getIconMaterial() {
        return this.iconMaterial;
    }

    public String getName() {
        return this.name;
    }
}