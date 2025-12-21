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

import com.admincmd.commandapi.*;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import org.bukkit.entity.Player;

@CommandHandler
public class TpAllCommand {

    @BaseCommand(command = "tpall", sender = Sender.PLAYER, permission = "admincmd.teleport.all", helpArguments = {"", "<-p player>"})
    public CommandResult executeTpAll(Player sender, CommandArgs args) {
        ACPlayer se = PlayerManager.getPlayer(sender);
        if (args.isEmpty()) {
            for (ACPlayer acp : PlayerManager.getOnlinePlayers()) {
                PlayerManager.teleport(se, acp);
            }

            return Messager.sendMessage(se, Locales.TELEPORT_ALL, Messager.MessageType.INFO);
        }

        if (args.hasFlag("p")) {
            if (!sender.hasPermission("admincmd.teleport.all.other")) {
                return CommandResult.NO_PERMISSION_OTHER;
            }

            CommandArgs.Flag f = args.getFlag("p");
            if (!f.isRegisteredPlayer()) {
                return CommandResult.NOT_A_PLAYER;
            }
            ACPlayer p = f.getPlayer();

            if (!p.isOnline()) {
                return CommandResult.NOT_ONLINE;
            }

            for (ACPlayer acp : PlayerManager.getOnlinePlayers()) {
                PlayerManager.teleport(p, acp);
            }

            return Messager.sendMessage(se, Locales.TELEPORT_ALL_OTHER.replacePlayer(p.getOfflinePlayer()), Messager.MessageType.INFO);
        }
        return CommandResult.ERROR;
    }

}
