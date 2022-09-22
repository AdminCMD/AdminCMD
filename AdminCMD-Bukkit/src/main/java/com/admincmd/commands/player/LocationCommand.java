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
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

@CommandHandler
public class LocationCommand {

    @BaseCommand(command = "loc", sender = Sender.PLAYER, permission = "admincmd.player.loc", aliases = "location,coords", helpArguments = {"", "<-p player>"})
    public CommandResult executeLocation(Player sender, CommandArgs args) {
        if (args.isEmpty()) {
            Location loc = sender.getLocation();
            DecimalFormat decimalFormat = new DecimalFormat("##.#");
            String msg = Locales.PLAYER_LOCATION_SELF.getString().replaceAll("%x%", decimalFormat.format(loc.getX()));
            msg = msg.replaceAll("%y%", decimalFormat.format(loc.getY()));
            msg = msg.replaceAll("%z%", decimalFormat.format(loc.getZ()));
            return Messager.sendMessage(PlayerManager.getPlayer(sender), msg, Messager.MessageType.INFO);
        }

        if (args.hasFlag("p")) {
            if (!sender.hasPermission("admincmd.player.loc.other")) {
                return CommandResult.NO_PERMISSION_OTHER;
            }

            CommandArgs.Flag flag = args.getFlag("p");
            if (!flag.isPlayerOnThisServer()) {
                return CommandResult.NOT_ONLINE;
            }

            Player target = flag.getPlayer().getPlayer();
            Location loc = target.getLocation();
            String msg = Locales.PLAYER_LOCATION_OTHER.getString().replaceAll("%player%", Utils.replacePlayerPlaceholders(target));
            DecimalFormat decimalFormat = new DecimalFormat("##.#");
            msg = msg.replaceAll("%x%", decimalFormat.format(loc.getX()));
            msg = msg.replaceAll("%y%", decimalFormat.format(loc.getY()));
            msg = msg.replaceAll("%z%", decimalFormat.format(loc.getZ()));
            return Messager.sendMessage(PlayerManager.getPlayer(sender), msg, Messager.MessageType.INFO);
        }

        return CommandResult.ERROR;

    }

}
