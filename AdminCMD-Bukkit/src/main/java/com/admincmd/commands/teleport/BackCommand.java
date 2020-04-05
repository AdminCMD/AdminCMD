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
package com.admincmd.commands.teleport;

import com.admincmd.commandapi.BaseCommand;
import com.admincmd.commandapi.CommandArgs;
import com.admincmd.commandapi.CommandArgs.Flag;
import com.admincmd.commandapi.CommandHandler;
import com.admincmd.commandapi.CommandResult;
import com.admincmd.commandapi.Sender;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import org.bukkit.entity.Player;

@CommandHandler
public class BackCommand {

    @BaseCommand(command = "back", sender = Sender.PLAYER, permission = "admincmd.teleport.back", helpArguments = {"", "<-p player>"})
    public CommandResult executeBack(Player sender, CommandArgs args) {
        ACPlayer p = PlayerManager.getPlayer(sender);
        if (args.isEmpty()) {
            if (!p.hasLastLoc()) {
                return Messager.sendMessage(p, Locales.TELEPORT_BACK_NO_LOCATION, Messager.MessageType.ERROR);
            }

            PlayerManager.teleport(p.getLastLoc(), p);
            return Messager.sendMessage(p, Locales.TELEPORT_BACK, Messager.MessageType.INFO);
        }

        if (args.hasFlag("p")) {
            if (!sender.hasPermission("admincmd.teleport.back.other")) {
                return CommandResult.NO_PERMISSION_OTHER;
            }

            Flag f = args.getFlag("p");
            if (!f.isRegisteredPlayer()) {
                return CommandResult.NOT_A_PLAYER;
            }
            ACPlayer bp = f.getPlayer();
            if (!bp.isOnline()) {
                return CommandResult.NOT_ONLINE;
            }

            if (!bp.hasLastLoc()) {
                return Messager.sendMessage(p, Locales.TELEPORT_BACK_NO_LOCATION, Messager.MessageType.ERROR);
            }

            PlayerManager.teleport(bp.getLastLoc(), bp);
            Messager.sendMessage(bp, Locales.TELEPORT_BACK, Messager.MessageType.INFO);
            return Messager.sendMessage(p, Locales.TELEPORT_BACK_OTHER.replacePlayer(bp.getOfflinePlayer()), Messager.MessageType.INFO);
        }
        return CommandResult.ERROR;
    }

}
