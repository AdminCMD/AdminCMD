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
import com.admincmd.teleport.RequestManager;
import com.admincmd.teleport.RequestType;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import org.bukkit.entity.Player;

@CommandHandler
public class TpaCommand {

    @BaseCommand(command = "tpa", sender = Sender.PLAYER, helpArguments = {"[yes|no]", "to -p <player>", "here -p <player>"})
    public CommandResult executeTpa(Player sender, CommandArgs args) {
        if (args.isEmpty()) {
            return CommandResult.ERROR;
        }

        String arg = args.getString(0);

        ACPlayer s = PlayerManager.getPlayer(sender);

        if (arg.equalsIgnoreCase("yes") && args.getLength() == 1) {
            if (!sender.hasPermission("admincmd.teleport.requests.accept")) {
                String msg = Locales.COMMAND_MESSAGES_NO_PERMISSION.getString().replaceAll("%perm%", "admincmd.teleport.requests.accept");
                return Messager.sendMessage(s, msg, Messager.MessageType.NONE);
            }
            if (!RequestManager.receiverHasRequest(s)) {
                return Messager.sendMessage(s, Locales.TELEPORT_TPA_NO_REQUEST, Messager.MessageType.ERROR);
            }

            RequestManager.acceptRequest(s);
            return CommandResult.SUCCESS;
        } else if (arg.equalsIgnoreCase("no") && args.getLength() == 1) {
            if (!sender.hasPermission("admincmd.teleport.requests.deny")) {
                String msg = Locales.COMMAND_MESSAGES_NO_PERMISSION.getString().replaceAll("%perm%", "admincmd.teleport.requests.deny");
                return Messager.sendMessage(s, msg, Messager.MessageType.NONE);
            }

            if (!RequestManager.receiverHasRequest(s)) {
                return Messager.sendMessage(s, Locales.TELEPORT_TPA_NO_REQUEST, Messager.MessageType.ERROR);
            }
            RequestManager.denyRequestFromReceiver(s);
            return CommandResult.SUCCESS;
        } else if (arg.equalsIgnoreCase("to") && args.hasFlag("p")) {
            if (!sender.hasPermission("admincmd.teleport.requests.to")) {
                String msg = Locales.COMMAND_MESSAGES_NO_PERMISSION.getString().replaceAll("%perm%", "admincmd.teleport.requests.to");
                return Messager.sendMessage(s, msg, Messager.MessageType.NONE);
            }

            Flag flag = args.getFlag("p");
            if (!flag.isRegisteredPlayer()) {
                return CommandResult.NOT_A_PLAYER;
            }

            ACPlayer receiver = flag.getPlayer();
            if (!receiver.isOnline()) {
                return CommandResult.NOT_ONLINE;
            }

            if (receiver.getID() == s.getID()) {
                return CommandResult.NOT_YOURSELF;
            }

            if (RequestManager.creatorHasRequest(s)) {
                RequestManager.denyRequestFromCreator(s);
            }

            RequestManager.createRequest(s, receiver, RequestType.TPTO);
            return CommandResult.SUCCESS;
        } else if (arg.equalsIgnoreCase("here") && args.hasFlag("p")) {
            if (!sender.hasPermission("admincmd.teleport.requests.here")) {
                String msg = Locales.COMMAND_MESSAGES_NO_PERMISSION.getString().replaceAll("%perm%", "admincmd.teleport.requests.here");
                return Messager.sendMessage(s, msg, Messager.MessageType.NONE);
            }

            Flag flag = args.getFlag("p");
            if (!flag.isRegisteredPlayer()) {
                return CommandResult.NOT_A_PLAYER;
            }

            ACPlayer receiver = flag.getPlayer();
            if (!receiver.isOnline()) {
                return CommandResult.NOT_ONLINE;
            }
            
            if (receiver.getID() == s.getID()) {
                return CommandResult.NOT_YOURSELF;
            }

            if (RequestManager.creatorHasRequest(s)) {
                RequestManager.denyRequestFromCreator(s);
            }

            RequestManager.createRequest(s, receiver, RequestType.TPHERE);
            return CommandResult.SUCCESS;
        } else {
            return CommandResult.ERROR;
        }
    }

}
