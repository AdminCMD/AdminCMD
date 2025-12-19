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
import com.admincmd.utils.Utils;
import org.bukkit.entity.Player;

@CommandHandler
public class ReplyCommand {

    @BaseCommand(command = "reply", sender = Sender.PLAYER, permission = "admincmd.player.reply", aliases = "r", helpArguments = "<message>", async = true)
    public CommandResult executeReply(Player sender, CommandArgs args) {
        ACPlayer se = PlayerManager.getPlayer(sender);
        int lastMsg = PlayerManager.getPlayer(sender).getLastMSGFrom();

        if (lastMsg == -1) {
            return Messager.sendMessage(se, Locales.PLAYER_REPLY_NO_LAST_MESSAGE, Messager.MessageType.ERROR);
        }

        if (args.getLength() >= 1) {
            ACPlayer target = PlayerManager.getPlayer(lastMsg);
            if (target == null) {
                return CommandResult.NOT_A_PLAYER;
            }

            if (!target.isOnline()) {
                return CommandResult.NOT_ONLINE;
            }

            StringBuilder message = new StringBuilder();
            for (String temp : args.getArgs()) {
                message.append(temp).append(" ");
            }

            target.setLastMSGFrom(se.getID());
            String msgSpy = Locales.PLAYER_MSG_FORMAT.getString().replaceAll("%sender%", Utils.replacePlayerPlaceholders(sender));
            msgSpy = msgSpy.replaceAll("%target%", Utils.replacePlayerPlaceholders(target.getOfflinePlayer()));
            msgSpy = msgSpy.replaceAll("%message%", message.toString());

            for (ACPlayer acp : PlayerManager.getOnlinePlayers()) {
                if (acp.isSpy()) {
                    Messager.sendMessage(acp, msgSpy, Messager.MessageType.NONE);
                }
            }

            String msgSender = Locales.PLAYER_MSG_FORMAT.getString().replaceAll("%sender%", Utils.replacePlayerPlaceholders(sender));
            msgSender = msgSender.replaceAll("%target%", Utils.replacePlayerPlaceholders(target.getOfflinePlayer()));
            msgSender = msgSender.replaceAll("%message%", message.toString());
            String msgTarget = Locales.PLAYER_MSG_FORMAT.getString().replaceAll("%target%", Utils.replacePlayerPlaceholders(target.getOfflinePlayer()));
            msgTarget = msgTarget.replaceAll("%sender%", Utils.replacePlayerPlaceholders(sender));
            msgTarget = msgTarget.replaceAll("%message%", message.toString());
            Messager.sendMessage(target, msgTarget, Messager.MessageType.NONE);
            return Messager.sendMessage(se, msgSender, Messager.MessageType.NONE);
        }

        return CommandResult.ERROR;

    }

}
