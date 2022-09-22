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
import org.bukkit.entity.Player;

@CommandHandler
public class FeedCommand {

    @BaseCommand(command = "feed", sender = Sender.PLAYER, permission = "admincmd.player.feed", helpArguments = {"", "<-p player>"})
    public CommandResult executeFeed(Player sender, CommandArgs args) {
        if (args.isEmpty()) {
            sender.setFoodLevel(15);
            return Messager.sendMessage(PlayerManager.getPlayer(sender), Locales.PLAYER_FEED_SELF, Messager.MessageType.INFO);
        }

        if (args.hasFlag("p")) {
            if (!sender.hasPermission("admincmd.player.feed.other")) {
                return CommandResult.NO_PERMISSION_OTHER;
            }
            CommandArgs.Flag flag = args.getFlag("p");
            if (!flag.isRegisteredPlayer()) {
                return CommandResult.NOT_A_PLAYER;
            }

            ACPlayer target = flag.getPlayer();

            if (!target.isOnline()) {
                return CommandResult.NOT_ONLINE;
            }

            if (PlayerManager.isOnThisServer(target)) {
                target.getPlayer().setFoodLevel(15);
            } else {
                BungeeCordMessageManager.getInstance().sendMessage(target, Channel.FEED_PLAYER, MessageCommand.FORWARD, "");
            }

            String msgSender = Locales.PLAYER_FEED_OTHER.getString().replaceAll("%player%", Utils.replacePlayerPlaceholders(target.getOfflinePlayer()));
            Messager.sendMessage(PlayerManager.getPlayer(sender), msgSender, Messager.MessageType.INFO);
            return Messager.sendMessage(target, Locales.PLAYER_FEED_SELF, Messager.MessageType.INFO);
        }

        return CommandResult.ERROR;
    }
}
