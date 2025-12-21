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
import com.admincmd.communication.BungeeCordMessageManager;
import com.admincmd.communication.Channel;
import com.admincmd.communication.MessageCommand;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@CommandHandler
public class GamemodeCommand {

    @BaseCommand(command = "gamemode", sender = Sender.PLAYER, permission = "admincmd.player.gamemode", aliases = "gm", helpArguments = {"", "<MODE>", "<-p player> <MODE>"})
    public CommandResult executeGamemode(final Player sender, CommandArgs args) {
        if (args.isEmpty()) {
            GameMode gm = sender.getGameMode() == GameMode.SURVIVAL ? GameMode.CREATIVE : GameMode.SURVIVAL;
            sender.setGameMode(gm);
            String msg = Locales.PLAYER_GAMEMODE_CHANGED.getString().replaceAll("%status%", gm.toString());
            Messager.sendMessage(PlayerManager.getPlayer(sender), msg, Messager.MessageType.INFO);
            return CommandResult.SUCCESS;
        }

        if (args.hasFlag("p")) {
            if (!sender.hasPermission("admincmd.player.gamemode.other")) {
                return CommandResult.NO_PERMISSION_OTHER;
            }
            CommandArgs.Flag flag = args.getFlag("p");
            if (!flag.isRegisteredPlayer()) {
                return CommandResult.NOT_A_PLAYER;
            }
            final ACPlayer actarget = flag.getPlayer();

            if (!actarget.isOnline()) {
                return CommandResult.NOT_ONLINE;
            }

            if (args.getLength() != 3) {
                return CommandResult.ERROR;
            }

            if (!args.isGameMode(2)) {
                return CommandResult.NOT_A_GAMEMODE;
            }

            final GameMode gm = args.getGameMode(2);

            if (PlayerManager.isOnThisServer(actarget)) {
                Player target = actarget.getPlayer();
                target.setGameMode(gm);
            } else {
                BungeeCordMessageManager.getInstance().sendMessage(actarget, Channel.GAMEMODE_PLAYER, MessageCommand.FORWARD, gm.toString());
            }
            String msg = Locales.PLAYER_GAMEMODE_CHANGED.getString().replaceAll("%status%", gm.toString());
            Messager.sendMessage(flag.getPlayer(), msg, Messager.MessageType.INFO);

            String msg2 = Locales.PLAYER_GAMEMODE_CHANGED_OTHER.getString().replaceAll("%player%", Utils.replacePlayerPlaceholders(actarget.getOfflinePlayer())).replaceAll("%status%", gm.toString());
            Messager.sendMessage(PlayerManager.getPlayer(sender), msg2, Messager.MessageType.INFO);
            return CommandResult.SUCCESS;
        } else {
            if (args.getLength() == 1) {
                if (!args.isGameMode(0)) {
                    return CommandResult.NOT_A_GAMEMODE;
                }

                final GameMode gm = args.getGameMode(0);
                sender.setGameMode(gm);
                String msg = Locales.PLAYER_GAMEMODE_CHANGED.getString().replaceAll("%status%", gm.toString());
                return Messager.sendMessage(PlayerManager.getPlayer(sender), msg, Messager.MessageType.INFO);
            }
        }

        return CommandResult.ERROR;
    }

}
