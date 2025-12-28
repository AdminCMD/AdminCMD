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
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandHandler
public class CommandWatcherCommand {

    @BaseCommand(command = "cw", sender = Sender.PLAYER, permission = "admincmd.player.cw", aliases = "cmdwatcher,commandwatcher", helpArguments = {"", "<-p player>"}, async = true)
    public CommandResult executeCW(Player sender, CommandArgs args) {
        ACPlayer p = PlayerManager.getPlayer(sender);
        if (args.isEmpty()) {
            boolean status = !p.isCMDWatcher();
            p.setCMDWatcher(!p.isCMDWatcher());
            String s = status ? Locales.COMMAND_MESSAGES_ENABLED.getString() : Locales.COMMAND_MESSAGES_DISABLED.getString();
            String msg = Locales.PLAYER_CW_TOGGLED.getString().replaceAll("%status%", s);
            return Messager.sendMessage(p, msg, Messager.MessageType.INFO);
        }

        if (args.hasFlag("p")) {           
            CommandArgs.Flag flag = args.getFlag("p");           
            ACPlayer target = flag.getPlayer();
            boolean status = !target.isCMDWatcher();
            target.setCMDWatcher(status);
            String s = status ? Locales.COMMAND_MESSAGES_ENABLED.getString() : Locales.COMMAND_MESSAGES_DISABLED.getString();
            String msg = Locales.PLAYER_CW_TOGGLED_OTHER.getString().replaceAll("%status%", s).replaceAll("%player%", Utils.replacePlayerPlaceholders(target.getOfflinePlayer()));
            String msg2 = Locales.PLAYER_CW_TOGGLED.getString().replaceAll("%status%", s);
            Messager.sendMessage(target, msg2, Messager.MessageType.INFO);
            return Messager.sendMessage(p, msg, Messager.MessageType.INFO);
        }
        return CommandResult.ERROR;
    }

    @TabComplete(command = "cw")
    public List<String> onTabComplete(CommandSender sender, CommandArgs args, List<String> tabs) {
        tabs.add("test");
        return tabs;
    }

}
