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
package com.admincmd.commands.home;

import com.admincmd.commandapi.*;
import com.admincmd.home.ACHome;
import com.admincmd.home.HomeManager;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import org.bukkit.entity.Player;

@CommandHandler
public class SethomeCommand {

    @BaseCommand(command = "sethome", sender = Sender.PLAYER, permission = "admincmd.home.set", aliases = "sh", helpArguments = "<name>")
    public CommandResult executeSethome(Player sender, CommandArgs args) {
        if (args.getLength() != 1) {
            return CommandResult.ERROR;
        }

        ACPlayer acp = PlayerManager.getPlayer(sender);

        ACHome h = HomeManager.getHome(acp, args.getString(0));
        if (h != null) {
            h.setLocation(acp.getLocation());
            return Messager.sendMessage(acp, Locales.HOME_UPDATED, Messager.MessageType.INFO);
        }

        HomeManager.createHome(acp, args.getString(0));
        return Messager.sendMessage(acp, Locales.HOME_SET, Messager.MessageType.INFO);
    }

}
