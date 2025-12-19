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
package com.admincmd.kits.commands;

import com.admincmd.commandapi.*;
import com.admincmd.kits.kits.ACKit;
import com.admincmd.kits.kits.KitManager;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import org.bukkit.entity.Player;

@CommandHandler
public class KitCommand {

    private static final HelpPage kitCmd = new HelpPage("kit ");

    static {
        kitCmd.addPage("<name>", "Opens the given kit");
        kitCmd.prepare();
    }

    @BaseCommand(command = "kit", sender = Sender.PLAYER, permission = "admincmd.kit")
    public CommandResult executeChest(final Player sender, CommandArgs args) {
        if (kitCmd.sendHelp(sender, args)) {
            return CommandResult.SUCCESS;
        }

        ACPlayer se = PlayerManager.getPlayer(sender);

        if (args.getLength() != 1) {
            return CommandResult.ERROR;
        }

        String name = args.getString(0);

        if (!sender.hasPermission("admincmd.kit." + name)) {
            return CommandResult.NO_PERMISSION;
        }

        ACKit kit = KitManager.getKit(name);
        if (kit == null) {
            sender.sendMessage("not existing");
            return CommandResult.SUCCESS;
        }

        KitManager.openKit(se, kit);
        return CommandResult.SUCCESS;
    }

}
