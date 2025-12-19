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
package com.admincmd.commands.spawn;

import com.admincmd.commandapi.*;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.spawn.SpawnManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.utils.MultiServerLocation;
import com.admincmd.utils.Utils;
import org.bukkit.entity.Player;

@CommandHandler
public class SpawnCommand {

    @BaseCommand(command = "spawn", sender = Sender.PLAYER, permission = "admincmd.spawn.spawn", helpArguments = {"", "<-p player>"}, async = true)
    public CommandResult executeSpawn(Player sender, CommandArgs args) {
        ACPlayer acp = PlayerManager.getPlayer(sender);
        if (args.isEmpty()) {
            MultiServerLocation loc = SpawnManager.getSpawn(acp);
            if (loc == null) {
                return CommandResult.ERROR;
            }
            PlayerManager.teleport(loc, acp);
            return Messager.sendMessage(acp, Locales.SPAWN_TP, Messager.MessageType.INFO);
        } else if (args.getLength() == 1) {
            if (!args.hasFlag("p")) {
                return CommandResult.ERROR;
            }

            if (!sender.hasPermission("admincmd.spawn.spawn.other")) {
                return CommandResult.NO_PERMISSION_OTHER;
            }

            CommandArgs.Flag f = args.getFlag("p");
            if (!f.isRegisteredPlayer()) {
                return CommandResult.NOT_A_PLAYER;
            }

            ACPlayer target = f.getPlayer();
            if (!target.isOnline()) {
                return CommandResult.NOT_ONLINE;
            }

            MultiServerLocation loc = SpawnManager.getSpawn(target);
            if (loc == null) {
                return CommandResult.ERROR;
            }

            PlayerManager.teleport(loc, target);
            Messager.sendMessage(target, Locales.SPAWN_TP, Messager.MessageType.INFO);
            Messager.sendMessage(acp, Locales.SPAWN_TP_OTHER.getString().replaceAll("%player%", Utils.replacePlayerPlaceholders(target.getOfflinePlayer())), Messager.MessageType.INFO);
            return CommandResult.SUCCESS;
        } else {
            return CommandResult.ERROR;
        }
    }

}
