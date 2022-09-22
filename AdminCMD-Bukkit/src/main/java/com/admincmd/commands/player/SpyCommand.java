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
public class SpyCommand {

    @BaseCommand(command = "spy", sender = Sender.PLAYER, permission = "admincmd.player.spy", aliases = "spymsg", helpArguments = {"", "<-p player>"})
    public CommandResult executeSpy(Player sender, CommandArgs args) {
        ACPlayer se = PlayerManager.getPlayer(sender);
        if (args.isEmpty()) {
            se.setSpy(!se.isSpy());
            String s = se.isSpy() ? Locales.COMMAND_MESSAGES_ENABLED.getString() : Locales.COMMAND_MESSAGES_DISABLED.getString();
            String msg = Locales.PLAYER_SPY_TOGGLED_SELF.getString().replaceAll("%status%", s);
            return Messager.sendMessage(se, msg, Messager.MessageType.INFO);
        }

        if (args.hasFlag("p")) {
            if (!sender.hasPermission("admincmd.player.spy.other")) {
                return CommandResult.NO_PERMISSION_OTHER;
            }

            CommandArgs.Flag flag = args.getFlag("p");
            if (!flag.isRegisteredPlayer()) {
                return CommandResult.NOT_A_PLAYER;
            }

            ACPlayer p = flag.getPlayer();
            p.setSpy(!p.isSpy());
            String s = p.isSpy() ? Locales.COMMAND_MESSAGES_ENABLED.getString() : Locales.COMMAND_MESSAGES_DISABLED.getString();
            String msgTarget = Locales.PLAYER_SPY_TOGGLED_SELF.getString().replaceAll("%status%", s);
            String msgSender = Locales.PLAYER_SPY_TOGGLED_OTHER.getString().replaceAll("%status%", s).replaceAll("%player%", Utils.replacePlayerPlaceholders(p.getOfflinePlayer()));
            Messager.sendMessage(p, msgTarget, Messager.MessageType.INFO);
            return Messager.sendMessage(se, msgSender, Messager.MessageType.INFO);
        }

        return CommandResult.ERROR;

    }

}
