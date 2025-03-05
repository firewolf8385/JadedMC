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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractCommand implements CommandExecutor {
    private final String permission;
    private final boolean canConsoleUse;
    private static JadedMCPlugin plugin;

    /**
     * Creates a new AbstractCommand.
     * @param commandName Name of the command.
     * @param permission Permission required to use the command.
     * @param canConsoleUse Whether console can use the command.
     */
    public AbstractCommand(@NotNull final String commandName, @NotNull final String permission, final boolean canConsoleUse) {
        this.permission = permission;
        this.canConsoleUse = canConsoleUse;
        Objects.requireNonNull(plugin.getCommand(commandName)).setExecutor(this);
    }

    /**
     * Registers all commands in the plugin.
     * @param pl Plugin.
     */
    public static void registerCommands(JadedMCPlugin pl) {
        plugin = pl;

        new AddExperienceCMD(pl);
        new AnvilCMD();
        new BroadcastCMD();
        new CancelWorldCMD(pl);
        new CenterCMD();
        //new ChatLogCMD(pl);
        new CommandSpyCMD(pl);
        new CopyWorldCMD(pl);
        new CreateWorldCMD(pl);
        new DiscordCMD();
        //new DuelsCMD();
        //new ECSeeCMD();
        //new EnderchestCMD();
        new FeedCMD();
        new FlyCMD();
        //new GameRulesCMD();
        //new GamesCMD();
        //new GrindstoneCMD();
        //new HealCMD();
        //new InstanceCMD();
        new InstancesCMD(pl);
        //new InvSeeCMD();
        //new JamisonCMD();
        new ListAllCMD(pl);
        new ListCMD(pl);
        new LoadWorldCMD(pl);
        new LobbyCMD();
        //new LoomCMD();
        new PlayersCMD(pl);
        new ProfileCMD(pl);
        //new RankCMD(pl);
        //new RebootCMD();
        //new RenameCMD();
        //new RulesCMD();
        //new SaveWorldCMD(pl);
        new SetLobbyCMD(pl);
        new StuckCMD(pl);
        //new StoreCMD();
        //new UUIDCMD();
        //new VanishCMD(pl);
        //new VoteCMD();
        //new WebsiteCMD();
        //new WorkbenchCMD();
    }

    /**
     * Executes the command.
     * @param sender The Command Sender.
     * @param args Arguments of the command.
     */
    public abstract void execute(@NotNull final CommandSender sender, final String[] args);

    /**
     * Processes early execution of the plugin.
     * @param sender Command Sender.
     * @param cmd The Command.
     * @param label Command Label.
     * @param args Command Arugments.
     * @return Successful Completion.
     */
    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command cmd, @NotNull final String label, final String[] args) {
        if(!permission.equals("") && !sender.hasPermission(permission)) {
            ChatUtils.chat(sender, "&cError &8» &cYou do not have access to that command.");
            return true;
        }
        if(!canConsoleUse && !(sender instanceof Player)) {
            ChatUtils.chat(sender, "&cError &8» &cOnly players can use that command.");
            return true;
        }
        execute(sender, args);
        return true;
    }
}