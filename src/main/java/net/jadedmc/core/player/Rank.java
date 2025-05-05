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

package net.jadedmc.core.player;

import net.jadedmc.utils.chat.ChatUtils;
import net.jadedmc.utils.chat.StringUtils;

/**
 * Represents a rank in the server.
 */
public enum Rank {
    OWNER("owner", 11, "<red>[Owner]</red>", "<white>", "<color:#fa7070>", "&c&lOwner"),
    ADMIN("admin", 10, "<red>[Admin]</red>", "<white>", "<color:#fa7070>", "&c&lAdmin"),
    MOD("mod", 9, "<color:#FF6E00>[Mod]</color>", "<white>", "<color:#E77D22>", "&6&lMod"),
    HELPER("helper", 8, "<color:#FF6E00>[Helper]</color>", "<white>", "<color:#E77D22>", "&6&lHelper"),
    BUILDER("builder", 7, "<color:#FFCC00>[Builder]</color>", "<white>", "<color:#FFDF06>", "&e&lBuilder"),
    DEVELOPER("developer", 6, "<color:#FFCC00>[Developer]", "<white>", "<color:#FFDF06>", "&e&lDeveloper"),
    YOUTUBE("youtube", 5, "<gradient:#ff0505:#ffffff><bold>YouTube</bold></gradient>", "<white>", "<gradient:#ff0505:#ffffff>", "&c&lYou&f&lTube"),
    JADED("jaded", 4, "<color:#92FE50>[Jaded]</color>", "<white>", "<color:#ABFE78>", "&a&lJaded"),
    SAPPHIRE("sapphire", 3, "<color:#387cfc>[Sapphire]</color>", "<white>", "<color:#0097ff>", "&9&lSapphire"),
    AMETHYST("amethyst", 2, "<color:#9662ca>[Amethyst]</color>", "<white>", "<color:#a77bd3>", "&5&lAmethyst"),
    GARNET("garnet", 1, "<gradient:#ff05aa:#fc31c2><bold>Garnet</bold></gradient>", "<white>", "<gradient:#ff05aa:#fc31c2>", "&d&lGarnet"),
    DEFAULT("default", 0, "<gray>Default</gray>", "<gray>", "<gray>", "");
    /*
    OWNER("owner", 11, "<gradient:#ff2c3a:#fa7070><bold>Owner</bold></gradient>", "<color:#fa7070>", "<gradient:#ff2c3a:#fa7070>", "&c&lOwner"),
    ADMIN("admin", 10, "<gradient:#ff2c3a:#fa7070><bold>Admin</bold></gradient>", "<color:#fa7070>", "<gradient:#ff2c3a:#fa7070>", "&c&lAdmin"),
    MOD("mod", 9, "<gradient:#ff5f05:#fa7c33><bold>Mod</bold></gradient>", "<color:#fa7c33>", "<gradient:#ff5f05:#fa7c33>", "&6&lMod"),
    HELPER("helper", 8, "<gradient:#ff5f05:#fa7c33><bold>Helper</bold></gradient>", "<color:#fa7c33>", "<gradient:#ff5f05:#fa7c33>", "&6&lHelper"),
    BUILDER("builder", 7, "<gradient:#ffb905:#ffd43a><bold>Builder</bold></gradient>", "<color:#ffd43a>", "<gradient:#ffb905:#ffd43a>", "&e&lBuilder"),
    DEVELOPER("developer", 6, "<gradient:#ffb905:#ffd43a><bold>Developer</bold></gradient>", "<color:#ffd43a>", "<gradient:#ffb905:#ffd43a>", "&e&lDeveloper"),
    YOUTUBE("youtube", 5, "<gradient:#ff0505:#ffffff><bold>YouTube</bold></gradient>", "<white>", "<gradient:#ff0505:#ffffff>", "&c&lYou&f&lTube"),
    JADED("jaded", 4, "<gradient:#0dff46:#acfa70><bold>Jaded</bold></gradient>", "<color:#acfa70>", "<gradient:#0dff46:#acfa70>", "&a&lJaded"),
    SAPPHIRE("sapphire", 3, "<gradient:#1657fe:#577cff><bold>Sapphire</bold></gradient>", "<color:#577cff>", "<gradient:#1657fe:#577cff>", "&9&lSapphire"),
    AMETHYST("amethyst", 2, "<gradient:#7605ff:#ac0ef6><bold>Amethyst</bold></gradient>", "<color:#ac0ef6>", "<gradient:#7605ff:#ac0ef6>", "&5&lAmethyst"),
    GARNET("garnet", 1, "<gradient:#ff05aa:#fc31c2><bold>Garnet</bold></gradient>", "<color:#fc31c2>", "<gradient:#ff05aa:#fc31c2>", "&d&lGarnet"),
    DEFAULT("default", 0, "<gray>Default</gray>", "<gray>", "<gray>", "");

     */

    private final String name;
    private final String displayName;
    private final String chatColor;
    private final String rankColor;
    private final int weight;
    private final String legacyDisplayName;

    /**
     * Creates the rank.
     * @param name Name of the rank.
     * @param weight Weight of the rank.
     * @param displayName Display name of the rank.
     * @param chatColor Chat color of the rank.
     */
    Rank(final String name, final int weight, final String displayName, final String chatColor, final String rankColor, final String legacyDisplayName) {
        this.name = name;
        this.weight = weight;
        this.displayName = displayName;
        this.chatColor = chatColor;
        this.rankColor = rankColor;
        this.legacyDisplayName = legacyDisplayName;
    }

    /**
     * Checks if a rank exists.
     * @param name Name of the rank to check.
     * @return If the rank exists.
     */
    public static boolean exists(String name) {

        for(Rank rank : values()) {
            if(rank.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get a rank from its name.
     * @param name Name of the rank.
     * @return Resulting rank.
     */
    public static Rank fromName(String name) {
        return Rank.valueOf(name.toUpperCase());
    }

    /**
     * Gets the rank's chat color.
     * @return Chat color of the rank.
     */
    public String getChatColor() {
        return chatColor;
    }

    /**
     * Gets the chat prefix of the rank,
     * @return Rank chat prefix.
     */
    public String getChatPrefix() {
        if(weight == 0) {
            return "";
        }

        return displayName + " ";
    }

    /**
     * Gets the display name of the rank.
     * @return Rank display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the name of the rank.
     * @return Rank's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the color of the rank.
     * @return Rank's color.
     */
    public String getRankColor() {
        return rankColor;
    }

    /**
     * Gets the weight of the rank.
     * @return Rank's weight.
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Get whether the rank is a Staff Rank.
     * @return If the rank is a staff rank.
     */
    public boolean isStaffRank() {
        return weight >= 5;
    }

    public String getLegacyDisplayName() {
        if(this.legacyDisplayName.isEmpty()) {
            return StringUtils.translateLegacyMessage(ChatUtils.toLegacy(this.displayName));
        }

        return this.legacyDisplayName;
    }
}