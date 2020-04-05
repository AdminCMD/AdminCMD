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
package com.admincmd.commands.maintenance;

import com.admincmd.Main;
import com.admincmd.commandapi.BaseCommand;
import com.admincmd.commandapi.CommandArgs;
import com.admincmd.commandapi.CommandHandler;
import com.admincmd.commandapi.CommandResult;
import com.admincmd.commandapi.Sender;
import com.admincmd.utils.Config;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandHandler
public class MaintenanceCommand {

    @BaseCommand(command = "maintenance", sender = Sender.ALL, permission = "admincmd.maintenance.toggle", helpArguments = {"", "enable", "disable"})
    public CommandResult executeMaintenance(CommandSender sender, CommandArgs args) {
        if (args.isEmpty()) {
            String s = Config.MAINTENANCE_ENABLE.getBoolean() ? Locales.COMMAND_MESSAGES_ENABLED.getString() : Locales.COMMAND_MESSAGES_DISABLED.getString();
            String msg = Locales.MAINTENANCE_TOGGLED.getString().replaceAll("%status%", s);
            sender.sendMessage(MessageType.INFO.getPrefix() + msg);
            return CommandResult.SUCCESS;
        }
        if (args.getString(0).equalsIgnoreCase("enable")) {
            if (Config.MAINTENANCE_ENABLE.getBoolean()) {
                String msg = Locales.MAINTENANCE_FAIL.getString().replaceAll("%status%", Locales.COMMAND_MESSAGES_ENABLED.getString());
                sender.sendMessage(MessageType.ERROR.getPrefix() + msg);
                return CommandResult.SUCCESS;
            }

            Config.MAINTENANCE_ENABLE.set(true, true);
            if (Config.MAINTENANCE_KICK.getBoolean()) {
                Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        for (Player p : Main.getInstance().getServer().getOnlinePlayers()) {
                            if (!p.hasPermission("admincmd.maintenance.bypass")) {
                                String msg = Locales.MESSAGE_PREFIX_ERROR.getString() + Locales.MAINTENANCE_KICK.getString().replaceAll("%perm%", "admincmd.maintenance.bypass");
                                p.kickPlayer(msg);
                            }
                        }
                    }
                });
            }

            String msg = Locales.MAINTENANCE_TOGGLED.getString().replaceAll("%status%", Locales.COMMAND_MESSAGES_ENABLED.getString());
            sender.sendMessage(MessageType.INFO.getPrefix() + msg);
            return CommandResult.SUCCESS;
        }
        if (args.getString(0).equalsIgnoreCase("disable")) {
            if (!Config.MAINTENANCE_ENABLE.getBoolean()) {
                String msg = Locales.MAINTENANCE_FAIL.getString().replaceAll("%status%", Locales.COMMAND_MESSAGES_DISABLED.getString());
                sender.sendMessage(MessageType.ERROR.getPrefix() + msg);
                return CommandResult.SUCCESS;
            }

            Config.MAINTENANCE_ENABLE.set(false, true);

            String msg = Locales.MAINTENANCE_TOGGLED.getString().replaceAll("%status%", Locales.COMMAND_MESSAGES_DISABLED.getString());
            sender.sendMessage(MessageType.INFO.getPrefix() + msg);
            return CommandResult.SUCCESS;
        }
        return CommandResult.ERROR;
    }
}
