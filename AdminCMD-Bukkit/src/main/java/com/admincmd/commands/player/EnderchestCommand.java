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
package com.admincmd.commands.player;

import com.admincmd.Main;
import com.admincmd.commandapi.BaseCommand;
import com.admincmd.commandapi.CommandArgs;
import com.admincmd.commandapi.CommandHandler;
import com.admincmd.commandapi.CommandResult;
import com.admincmd.commandapi.Sender;
import com.admincmd.player.ACPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandHandler
public class EnderchestCommand {

    @BaseCommand(command = "enderchest", sender = Sender.PLAYER, permission = "admincmd.player.enderchest", aliases = "ec", helpArguments = {"", "<-p player>"})
    public CommandResult executeEnderchest(final Player sender, CommandArgs args) {
        if (args.isEmpty()) {
            Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    sender.openInventory(sender.getEnderChest());
                }
            });
            return CommandResult.SUCCESS;
        }

        if (args.hasFlag("p")) {
            if (!sender.hasPermission("admincmd.player.enderchest.other")) {
                return CommandResult.NO_PERMISSION_OTHER;
            }
            CommandArgs.Flag flag = args.getFlag("p");
            if (!flag.isPlayerOnThisServer()) {
                return CommandResult.NOT_ONLINE;
            }

            ACPlayer target = flag.getPlayer();
            final Player bTarget = target.getPlayer();
            Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    sender.openInventory(bTarget.getEnderChest());
                }
            });
            return CommandResult.SUCCESS;
        }

        return CommandResult.ERROR;
    }

}
