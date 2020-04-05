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
import com.admincmd.commandapi.CommandHandler;
import com.admincmd.commandapi.CommandResult;
import com.admincmd.commandapi.Sender;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.utils.MultiServerLocation;
import com.admincmd.world.ACWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandHandler
public class TPCommand {

    @BaseCommand(command = "tp", sender = Sender.PLAYER, aliases = "teleport", helpArguments = {"<x y z>", "<world> <x y z>", "<player>", "<player> <player>"}, permission = "admincmd.tp.teleport")
    public CommandResult executeTp(Player sender, CommandArgs args) {
        if (args.isEmpty()) {
            return CommandResult.ERROR;
        }

        ACPlayer s = PlayerManager.getPlayer(sender);
        if (args.getLength() == 3) {
            if (!args.isInteger(0) || !args.isInteger(1) || !args.isInteger(2)) {
                return CommandResult.NOT_A_NUMBER;
            }

            int x = args.getInt(0);
            int y = args.getInt(1);
            int z = args.getInt(2);
            Location target = new Location(sender.getWorld(), x, y, z);
            MultiServerLocation tar = MultiServerLocation.fromLocation(target);
            PlayerManager.teleport(tar, s);
            return CommandResult.SUCCESS;
        } else if (args.getLength() == 1) {
            if (!args.isRegisteredPlayer(0)) {
                return CommandResult.NOT_A_PLAYER;
            }
            ACPlayer target = args.getPlayer(0);
            if (!target.isOnline()) {
                return CommandResult.NOT_ONLINE;
            }
            PlayerManager.teleport(target, s);
            Messager.sendMessage(s, Locales.TELEPORT_TPA_ACCEPT_REQUEST.replacePlayer(target.getOfflinePlayer()), Messager.MessageType.INFO);
            return CommandResult.SUCCESS;
        } else if (args.getLength() == 2) {
            if (!args.isRegisteredPlayer(0) || !args.isRegisteredPlayer(1)) {
                return CommandResult.NOT_A_PLAYER;
            }

            ACPlayer toTP = args.getPlayer(0);
            ACPlayer target = args.getPlayer(1);
            if (!toTP.isOnline() || !target.isOnline()) {
                return CommandResult.NOT_ONLINE;
            }
            PlayerManager.teleport(target, toTP);
            return CommandResult.SUCCESS;
        } else if (args.getLength() == 4) {
            if (!args.isWorld(0)) {
                return CommandResult.NOT_A_WORLD;
            }

            if (!args.isInteger(1) || !args.isInteger(2) || !args.isInteger(3)) {
                return CommandResult.NOT_A_NUMBER;
            }

            ACWorld world = args.getWorld(0);
            int x = args.getInt(1);
            int y = args.getInt(2);
            int z = args.getInt(3);
            MultiServerLocation loc = new MultiServerLocation(x, y, z, world);
            PlayerManager.teleport(loc, s);
            return CommandResult.SUCCESS;
        } else {
            return CommandResult.ERROR;
        }
    }

    @BaseCommand(command = "tphere", sender = Sender.PLAYER, helpArguments = {"<player>"}, permission = "admincmd.tp.teleport")
    public CommandResult executeTpHere(Player sender, CommandArgs args) {
        if (args.getLength() != 1) {
            return CommandResult.ERROR;
        }

        ACPlayer s = PlayerManager.getPlayer(sender);
        if (!args.isRegisteredPlayer(0)) {
            return CommandResult.NOT_A_PLAYER;
        }

        ACPlayer target = args.getPlayer(0);
        if (!target.isOnline()) {
            return CommandResult.NOT_ONLINE;
        }
        PlayerManager.teleport(s, target);
        Messager.sendMessage(s, Locales.TELEPORT_TPA_ACCEPT_TARGET.replacePlayer(target.getOfflinePlayer()), Messager.MessageType.INFO);
        return CommandResult.SUCCESS;
    }

}
