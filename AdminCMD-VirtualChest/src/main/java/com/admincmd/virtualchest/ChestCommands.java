/*
 * This file is part of AdminCMD
 * Copyright (C) 2020 AdminCMD Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.admincmd.virtualchest;

import com.admincmd.Main;
import com.admincmd.commandapi.*;
import com.admincmd.commandapi.CommandArgs.Flag;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Messager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@CommandHandler
public class ChestCommands {

    private static final HelpPage chest = new HelpPage("chest");
    private static final HelpPage clearchest = new HelpPage("clearchest");

    static {
        chest.addPage("", "Opens your Virtual chest");
        chest.addPage(" <-p player>", "Opens the virtual chest of the given player");
        chest.prepare();

        clearchest.addPage("", "Clears your virtual chest");
        clearchest.addPage(" <-p player>", "Clears the virtual chest of the given player");
        clearchest.prepare();
    }

    @BaseCommand(command = "clearchest", sender = Sender.PLAYER, permission = "admincmd.virtualchest.clearchest", aliases = "cc")
    public CommandResult executeClearChest(Player sender, CommandArgs args) {
        if (clearchest.sendHelp(sender, args)) {
            return CommandResult.SUCCESS;
        }

        ACPlayer se = PlayerManager.getPlayer(sender);

        if (args.isEmpty()) {
            ACChest c = ChestManager.getChest(se);
            c.clear();
            return Messager.sendMessage(se, "Successfully cleared your chest.", Messager.MessageType.INFO);
        } else {
            if (args.hasFlag("p")) {
                if (!sender.hasPermission("admincmd.virtualchest.clearchest.other")) {
                    return CommandResult.NO_PERMISSION_OTHER;
                }

                Flag f = args.getFlag("p");
                if (!f.isRegisteredPlayer()) {
                    return CommandResult.NOT_A_PLAYER;
                }

                ACPlayer bp = f.getPlayer();
                ACChest c = ChestManager.getChest(bp);
                c.clear();
                return Messager.sendMessage(se, "Successfully cleared the chest of " + bp.getName(), Messager.MessageType.INFO);
            }
        }

        return CommandResult.ERROR;
    }

    @BaseCommand(command = "chest", sender = Sender.PLAYER, permission = "admincmd.virtualchest.chest", aliases = "vc")
    public CommandResult executeChest(final Player sender, CommandArgs args) {
        if (chest.sendHelp(sender, args)) {
            return CommandResult.SUCCESS;
        }

        ACPlayer se = PlayerManager.getPlayer(sender);

        if (args.isEmpty()) {
            ACChest c = ChestManager.getChest(se);
            final Inventory inv = c.getInventory();
            Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    sender.openInventory(inv);
                }
            });
            return CommandResult.SUCCESS;
        } else {
            if (args.hasFlag("p")) {
                if (!sender.hasPermission("admincmd.virtualchest.chest.other")) {
                    return CommandResult.NO_PERMISSION_OTHER;
                }

                Flag f = args.getFlag("p");
                if (!f.isRegisteredPlayer()) {
                    return CommandResult.NOT_A_PLAYER;
                }

                ACPlayer bp = f.getPlayer();
                ACChest c = ChestManager.getChest(bp);
                final Inventory inv = c.getInventory();
                Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        sender.openInventory(inv);
                    }
                });
                return CommandResult.SUCCESS;
            }
        }

        return CommandResult.ERROR;
    }

}
