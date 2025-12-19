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
public class MsgCommand {

    @BaseCommand(command = "msg", sender = Sender.PLAYER, permission = "admincmd.player.msg", aliases = "pm,message", helpArguments = {"<player> <message>"}, async = true)
    public CommandResult executeMsg(Player sender, CommandArgs args) {
        if (args.getLength() >= 2) {
            if (!args.isRegisteredPlayer(0)) {
                return CommandResult.NOT_A_PLAYER;
            }

            String message = "";
            for (int i = 1; i < args.getLength(); i++) {
                message += args.getString(i) + " ";
            }

            ACPlayer target = args.getPlayer(0);

            if (!target.isOnline()) {
                return CommandResult.NOT_ONLINE;
            }

            ACPlayer se = PlayerManager.getPlayer(sender);
            target.setLastMSGFrom(se.getID());
            String msgSpy = Locales.PLAYER_MSG_FORMAT.getString().replaceAll("%sender%", Utils.replacePlayerPlaceholders(sender));
            msgSpy = msgSpy.replaceAll("%target%", Utils.replacePlayerPlaceholders(target.getOfflinePlayer()));
            msgSpy = msgSpy.replaceAll("%message%", message);

            for (ACPlayer acp : PlayerManager.getOnlinePlayers()) {
                if (acp.isSpy()) {
                    Messager.sendMessage(acp, msgSpy, Messager.MessageType.NONE);
                }
            }

            String msgSender = Locales.PLAYER_MSG_FORMAT.getString().replaceAll("%sender%", Utils.replacePlayerPlaceholders(sender));
            msgSender = msgSender.replaceAll("%target%", Utils.replacePlayerPlaceholders(target.getOfflinePlayer()));
            msgSender = msgSender.replaceAll("%message%", message);
            String msgTarget = Locales.PLAYER_MSG_FORMAT.getString().replaceAll("%target%", Utils.replacePlayerPlaceholders(target.getOfflinePlayer()));
            msgTarget = msgTarget.replaceAll("%sender%", Utils.replacePlayerPlaceholders(sender));
            msgTarget = msgTarget.replaceAll("%message%", message);
            Messager.sendMessage(target, msgTarget, Messager.MessageType.NONE);
            return Messager.sendMessage(se, msgSender, Messager.MessageType.NONE);
        }

        return CommandResult.ERROR;

    }

}
