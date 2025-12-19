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

import com.admincmd.commandapi.*;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.utils.Messager.MessageType;
import com.admincmd.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandHandler
public class ListCommand {

    @BaseCommand(command = "who", sender = Sender.PLAYER, permission = "admincmd.player.list", aliases = "plist,online,list", helpArguments = "", async = true)
    public CommandResult executeList(Player sender, CommandArgs args) {
        ACPlayer se = PlayerManager.getPlayer(sender);
        if (args.isEmpty()) {
            String playerList = "";
            for (ACPlayer p : PlayerManager.getOnlinePlayers()) {
                if (!p.isInvisible()) {
                    playerList += Utils.replacePlayerPlaceholders(p.getOfflinePlayer()) + ", ";
                }
            }
            String msg = Locales.PLAYER_LIST_FORMAT.getString().replaceAll("%playerList%", playerList);
            return Messager.sendMessage(se, msg, Messager.MessageType.NONE);
        }
        return CommandResult.ERROR;
    }

    @BaseCommand(command = "who", sender = Sender.CONSOLE, permission = "admincmd.player.list", aliases = "plist,online,list", helpArguments = "", async = true)
    public CommandResult executeListConsole(CommandSender sender, CommandArgs args) {
        if (args.isEmpty()) {
            String playerList = "";
            for (ACPlayer p : PlayerManager.getOnlinePlayers()) {
                playerList += Utils.replacePlayerPlaceholders(p.getOfflinePlayer()) + ", ";
            }
            String msg = Locales.PLAYER_LIST_FORMAT.getString().replaceAll("%playerList%", playerList);
            sender.sendMessage(MessageType.INFO.getPrefix() + msg);
            return CommandResult.SUCCESS;
        }
        return CommandResult.ERROR;
    }
}
